package com.laba3.CourseAggregator.services;

import com.laba3.CourseAggregator.entities.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Service("nacbank")
public class NacBankApiServiceImpl implements BankApiService{
    private static final Logger logger = LoggerFactory.getLogger(NacBankApiServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${nacbank.url}")
    private String nacbankUrl;

    @Async("asyncCourseExecutor")
    @Override
    public CompletableFuture<Course> getCurrentCourse(String currency) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        ResponseEntity<Course[]> courseList = restTemplate.getForEntity(nacbankUrl + "?" + "json" + "&valcode=" + currency + "&date=" + date, Course[].class);
        for (Course course : courseList.getBody()) {
            if(course.getCurrency().equals(currency)) {
                course.setBank("Nacbank");
                logger.debug("execute getCurrentCourse method with " + course);
                return CompletableFuture.completedFuture(course);
            }
        }
        logger.debug("execute getCurrentCourse method with NULL");
        return null;
    }

    @Async("asyncCourseExecutor")
    @Override
    public CompletableFuture<Course> getArchiveCourse(String date, String currency) {
        if(date.equals("yesterday")) {
            date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            logger.debug("executing getArchiveCourse method and get date: " + date);
        } else {
            date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy")).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        ResponseEntity<Course[]> courseList = restTemplate.getForEntity(nacbankUrl + "?" + "json" + "&date=" + date, Course[].class);

        for (Course course : courseList.getBody()) {
            if(course.getCurrency().equals(currency)) {
                course.setBank("Nacbank");
                logger.debug("execute getArchiveCourse method with " + course);
                return CompletableFuture.completedFuture(course);
            }
        }
        logger.debug("execute getArchiveCourse method with NULL");
        return null;
    }
}
