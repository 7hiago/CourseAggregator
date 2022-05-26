package com.laba3.CourseAggregator.services;

import com.laba3.CourseAggregator.entities.Course;
import com.laba3.CourseAggregator.entities.PrivatbankArchiveApiData;
import com.laba3.CourseAggregator.exceptions.ApiTimeoutException;
import com.laba3.CourseAggregator.exceptions.CourseNotFoundException;
import com.laba3.CourseAggregator.utils.CourseExtractor;
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
    public CompletableFuture<Course> getCurrentCourse(String currency) throws ApiTimeoutException, CourseNotFoundException {
        ResponseEntity<Course[]> courseList = restTemplate.getForEntity(privatbankCurrentUrl, Course[].class);
        Course course = CourseExtractor.extract(courseList.getBody(), currency);
        course.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        course.setBank("Privatbank");
        return CompletableFuture.completedFuture(course);
    }

    @Async("asyncCourseExecutor")
    @Override
    public CompletableFuture<Course> getArchiveCourse(String date, String currency) {
        ResponseEntity<PrivatbankArchiveApiData> privatbankArchiveData = restTemplate.getForEntity(privatbankArchiveUrl + "?" + "json" + "&date=" + date, PrivatbankArchiveApiData.class);
        Course course = CourseExtractor.extract(privatbankArchiveData.getBody().getCourses().toArray(new Course[0]), currency);
        course.setDate(privatbankArchiveData.getBody().getDate());
        course.setBank("Privatbank");
        return CompletableFuture.completedFuture(course);
    }
}
