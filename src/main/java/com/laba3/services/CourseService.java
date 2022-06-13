package com.laba3.services;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.laba3.entities.Course;
import com.laba3.exceptions.ApiInternalException;
import com.laba3.exceptions.CourseNotFoundException;
import com.laba3.exceptions.DateWrongParametersException;
import com.laba3.utils.DateValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeParseException;
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
        Course course;

        try {
            course = nacBankApiServiceImpl.getCurrentCourse(currency).get();
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

    @Cacheable(value = "courses-nacbank", key = "{#date, #currency }")
    public Course getNacbankArchiveCourse(String date, String currency) {
        logger.debug("execute getNacbankArchiveCourse method");
        Course course;

        try {
            date = dateValidation.validate(date, "yyyyMMdd");
        } catch (DateTimeParseException e) {
            throw new DateWrongParametersException(date + " date format is not available. Use dd.MM.yyyy format!");
        } catch (DatatypeException e) {
            throw new DateWrongParametersException(date + " wrong date. Date must be before today!");
        }

        try {
            course = nacBankApiServiceImpl.getArchiveCourse(date, currency).get();
            if(course == null) {
                logger.warn(currency + " currency is not available in this bank");
                throw new CourseNotFoundException(currency + " currency is not available in this bank");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            throw new ApiInternalException(e.getMessage());

        }

        return course;
    }

    @Cacheable(value = "courses-privatbank", key = "#currency")
    public Course getPrivatbankCurrentCourse(String currency) {
        logger.debug("execute getPrivatbankCurrentCourse method");
        Course course;

        try {
            course = privatBankApiServiceImpl.getCurrentCourse(currency).get();
            if(course == null) {
                logger.warn(currency + " currency is not available in this bank");
                throw new CourseNotFoundException(currency + " currency is not available in this bank");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            throw new ApiInternalException(e.getMessage());
        }

        return course;
    }

    @Cacheable(value = "courses-privatbank", key = "{#date, #currency }")
    public Course getPrivatbankArchiveCourse(String date, String currency) {
        logger.debug("execute getPrivatbankArchiveCourse method");
        Course course;

        try {
            date = dateValidation.validate(date, "dd.MM.yyyy");
        } catch (DateTimeParseException e) {
            throw new DateWrongParametersException(date + " date format is not available. Use dd.MM.yyyy format!");
        } catch (DatatypeException e) {
            throw new DateWrongParametersException(date + " wrong date. Date must be before today!");
        }

        try {
            course = privatBankApiServiceImpl.getArchiveCourse(date, currency).get();
            if(course == null) {
                logger.warn(currency + " currency is not available in this bank");
                throw new CourseNotFoundException(currency + " currency is not available in this bank");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            throw new ApiInternalException(e.getMessage());
        }

        return course;
    }

    @Cacheable(value = "courses-monobank", key = "#currency")
    public Course getMonobankCurrentCourse(String currency) {
        logger.debug("execute getMonobankCurrentCourse method");
        Course course;

        try {
            course = monoBankApiServiceImpl.getCurrentCourse(currency).get();
            if(course == null) {
                logger.warn(currency + " currency is not available in this bank");
                throw new CourseNotFoundException(currency + " currency is not available in this bank");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            throw new ApiInternalException(e.getMessage());
        }

        return course;
    }
}
