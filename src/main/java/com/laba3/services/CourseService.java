package com.laba3.services;

import com.laba3.entities.Course;
import com.laba3.exceptions.ApiInternalException;
import com.laba3.exceptions.CourseNotFoundException;
import com.laba3.utils.DateValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CourseService {
    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    private final BankApiService nacBankApiServiceImpl;
    private final BankApiService privatBankApiServiceImpl;
    private final BankApiService monoBankApiServiceImpl;
    private final DateValidation dateValidation;

    public CourseService(@Qualifier("nacbank") BankApiService nacBankApiServiceImpl,
                         @Qualifier("privatbank") BankApiService privatBankApiServiceImpl,
                         @Qualifier("monobank") BankApiService monoBankApiServiceImpl,
                         DateValidation dateValidation) {
        this.nacBankApiServiceImpl = nacBankApiServiceImpl;
        this.privatBankApiServiceImpl = privatBankApiServiceImpl;
        this.monoBankApiServiceImpl = monoBankApiServiceImpl;
        this.dateValidation = dateValidation;
    }

    @Cacheable(value = "courses-nacbank", key = "#currency")
    public Course getNacbankCurrentCourse(String currency) {
        logger.debug("execute getNacbankCurrentCourse method");
        return getCourse(currency, nacBankApiServiceImpl.getCurrentCourse(currency));
    }

    @Cacheable(value = "courses-nacbank", key = "{#date, #currency }")
    public Course getNacbankArchiveCourse(String date, String currency) {
        logger.debug("execute getNacbankArchiveCourse method");
        String validatedDate = dateValidation.validate(date, "yyyyMMdd");
        return getCourse(currency, nacBankApiServiceImpl.getArchiveCourse(validatedDate, currency));
    }

    @Cacheable(value = "courses-privatbank", key = "#currency")
    public Course getPrivatbankCurrentCourse(String currency) {
        logger.debug("execute getPrivatbankCurrentCourse method");
        return getCourse(currency, privatBankApiServiceImpl.getCurrentCourse(currency));
    }

    @Cacheable(value = "courses-privatbank", key = "{#date, #currency }")
    public Course getPrivatbankArchiveCourse(String date, String currency) {
        logger.debug("execute getPrivatbankArchiveCourse method");
        String validatedDate = dateValidation.validate(date, "dd.MM.yyyy");
        return getCourse(currency, privatBankApiServiceImpl.getArchiveCourse(validatedDate, currency));
    }

    @Cacheable(value = "courses-monobank", key = "#currency")
    public Course getMonobankCurrentCourse(String currency) {
        logger.debug("execute getMonobankCurrentCourse method");
        return getCourse(currency, monoBankApiServiceImpl.getCurrentCourse(currency));
    }

    private Course getCourse(String currency, CompletableFuture<Course> bankApiServiceMethod) {
        Course course;

        try {
            course = bankApiServiceMethod.get();
            if(course == null) {
                logger.warn(currency + " currency is not available in this bank");
                throw new CourseNotFoundException(currency + " currency is not available in this bank");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.debug(e.getMessage());
            throw new ApiInternalException(e.getMessage());
        }

        return course;
    }
}
