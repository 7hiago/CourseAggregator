package com.laba3.CourseAggregator.services;

import com.laba3.CourseAggregator.entities.Course;
import com.laba3.CourseAggregator.exceptions.ApiTimeoutException;
import com.laba3.CourseAggregator.exceptions.CourseNotFoundException;
import com.laba3.CourseAggregator.exceptions.DateWrongParametersException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CourseService {
    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    private final BankApiService nacBankApiServiceImpl;
    private final BankApiService privatBankApiServiceImpl;
    private final BankApiService monoBankApiServiceImpl;

    public CourseService(@Qualifier("nacbank") BankApiService nacBankApiServiceImpl,
                         @Qualifier("privatbank") BankApiService privatBankApiServiceImpl,
                         @Qualifier("monobank") BankApiService monoBankApiServiceImpl) {
        this.nacBankApiServiceImpl = nacBankApiServiceImpl;
        this.privatBankApiServiceImpl = privatBankApiServiceImpl;
        this.monoBankApiServiceImpl = monoBankApiServiceImpl;
    }

    @Cacheable(value = "courses-nacbank", key = "#currency")
    public Course getNacbankCurrentCourse(String currency) {
        try {
            logger.debug("execute getNacbankCurrentCourse method");
            Course course = nacBankApiServiceImpl.getCurrentCourse(currency).get();
            if(course == null) {
                logger.warn(currency + " currency is not available in this bank");
                throw new CourseNotFoundException(currency + " currency is not available in this bank");
            }
            return course;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        logger.debug("execute getNacbankCurrentCourse method with NULL");
        return null;
    }

    @Cacheable(value = "courses-nacbank", key = "{#date, #currency }")
    public Course getNacbankArchiveCourse(String date, String currency) {
        try {
            logger.debug("execute getNacbankArchiveCourse method");
            Course course = nacBankApiServiceImpl.getArchiveCourse(date, currency).get();
            if(course == null) {
                logger.warn(currency + " currency is not available in this bank");
                throw new CourseNotFoundException(currency + " currency is not available in this bank");
            }
            return course;
       } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
        logger.debug("execute getNacbankArchiveCourse method with NULL");
        return null;
    }

    @Cacheable(value = "courses-privatbank", key = "#currency")
    public Course getPrivatbankCurrentCourse(String currency) {
        try {
            logger.debug("execute getPrivatbankCurrentCourse method");
            return privatBankApiServiceImpl.getCurrentCourse(currency).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
        logger.debug("execute getPrivatbankCurrentCourse method with NULL");
        return null;
    }

    @Cacheable(value = "courses-privatbank", key = "{#date, #currency }")
    public Course getPrivatbankArchiveCourse(String date, String currency) {
        try {
            logger.debug("execute getPrivatbankArchiveCourse method");
            return privatBankApiServiceImpl.getArchiveCourse(date, currency).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
        logger.debug("execute getPrivatbankArchiveCourse method with NULL");
        return null;
    }

    @Cacheable(value = "courses-monobank", key = "#currency")
    public Course getMonobankCurrentCourse(String currency) {
        try {
            logger.debug("execute getMonobankCurrentCourse method");
            return monoBankApiServiceImpl.getCurrentCourse(currency).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
        logger.debug("execute getMonobankCurrentCourse method with NULL");
        return null;
    }

    public Course getBestCourse(String date, String currency) {
        LocalDate localDate;
        if(date.equals("today")) {
            localDate = LocalDate.now();
            logger.debug("executing getBestCourse method and get date: " + localDate + " from " + date);
        } else {
            localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
        List<Course> courseList = new ArrayList<>();
        if(localDate.equals(LocalDate.now())) {
            courseList.add(getNacbankCurrentCourse(currency));
            courseList.add(getPrivatbankCurrentCourse(currency));
            courseList.add(getMonobankCurrentCourse(currency));
        } else if(localDate.isBefore(LocalDate.now())) {
            courseList.add(getNacbankArchiveCourse(date, currency));
            courseList.add(getPrivatbankArchiveCourse(date, currency));
        } else {
            logger.debug("date must be before or equals today");
        }
        Collections.sort(courseList);

        logger.debug("execute getBestCourse method with " + courseList.get(0));
        return courseList.get(0);
    }
}
