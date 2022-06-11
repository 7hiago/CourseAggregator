package com.laba3.services;

import com.laba3.entities.Course;
import com.laba3.entities.PrivatbankArchiveApiData;
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

@Service("privatbank")
public class PrivatBankApiServiceImpl implements BankApiService {
    private static final Logger logger = LoggerFactory.getLogger(PrivatBankApiServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${privatbank.current.url}")
    private String privatbankCurrentUrl;

    @Value("${privatbank.archive.url}")
    private String privatbankArchiveUrl;

    @Async("asyncCourseExecutor")
    @Override
    public CompletableFuture<Course> getCurrentCourse(String currency) {
        ResponseEntity<Course[]> courseList = restTemplate.getForEntity(privatbankCurrentUrl, Course[].class);
        Course course = CourseExtractor.extract(Objects.requireNonNull(courseList.getBody()), currency);
        if(course == null) {
            return null;
        }
        course.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        course.setBank("Privatbank");
        return CompletableFuture.completedFuture(course);
    }

    @Async("asyncCourseExecutor")
    @Override
    public CompletableFuture<Course> getArchiveCourse(String date, String currency) {
        ResponseEntity<PrivatbankArchiveApiData> privatbankArchiveData = restTemplate.getForEntity(privatbankArchiveUrl + "?" + "json" + "&date=" + date, PrivatbankArchiveApiData.class);
        Course course = CourseExtractor.extract(Objects.requireNonNull(privatbankArchiveData.getBody()).getCourses().toArray(new Course[0]), currency);
        if(course == null) {
            return null;
        }
        course.setDate(privatbankArchiveData.getBody().getDate());
        course.setBank("Privatbank");
        return CompletableFuture.completedFuture(course);
    }
}
