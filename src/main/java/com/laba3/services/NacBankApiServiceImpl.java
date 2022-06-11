package com.laba3.services;

import com.laba3.entities.Course;
import com.laba3.utils.CourseExtractor;
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
import java.util.Objects;
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
        Course course = CourseExtractor.extract(Objects.requireNonNull(courseList.getBody()), currency);
        if(course == null) {
            return null;
        }
        course.setBank("Nacbank");
        return CompletableFuture.completedFuture(course);
    }

    @Async("asyncCourseExecutor")
    @Override
    public CompletableFuture<Course> getArchiveCourse(String date, String currency) {
        ResponseEntity<Course[]> courseList = restTemplate.getForEntity(nacbankUrl + "?" + "json" + "&date=" + date, Course[].class);
        Course course = CourseExtractor.extract(Objects.requireNonNull(courseList.getBody()), currency);
        if(course == null) {
            return null;
        }
        course.setBank("Nacbank");
        return CompletableFuture.completedFuture(course);
    }
}
