package com.laba3.CourseAggregator.services;

import com.laba3.CourseAggregator.entities.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FeatureCourseService {
    private static final Logger logger = LoggerFactory.getLogger(FeatureCourseService.class);

    private final CourseService courseService;

    public FeatureCourseService(CourseService courseService) {
        this.courseService = courseService;
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
            courseList.add(courseService.getNacbankCurrentCourse(currency));
            courseList.add(courseService.getPrivatbankCurrentCourse(currency));
            courseList.add(courseService.getMonobankCurrentCourse(currency));
        } else if(localDate.isBefore(LocalDate.now())) {
            courseList.add(courseService.getNacbankArchiveCourse(date, currency));
            courseList.add(courseService.getPrivatbankArchiveCourse(date, currency));
        } else {
            logger.debug("date must be before or equals today");
        }
        Collections.sort(courseList);

        logger.debug("execute getBestCourse method with " + courseList.get(0));
        return courseList.get(0);
    }
}
