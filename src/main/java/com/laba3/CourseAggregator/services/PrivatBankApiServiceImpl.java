package com.laba3.CourseAggregator.services;

import com.laba3.CourseAggregator.entities.Course;
import com.laba3.CourseAggregator.entities.PrivatbankArchiveApiData;
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
    public CompletableFuture<Course> getCurrentCourse(String currency) {
        ResponseEntity<Course[]> courseList = restTemplate.getForEntity(privatbankCurrentUrl, Course[].class);
        for (Course course : courseList.getBody()) {
            if(course.getCurrency().equals(currency)) {
                course.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                course.setBank("Privatbank");
                logger.debug("execute getCurrentCourse method " + course);
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
            date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
        ResponseEntity<PrivatbankArchiveApiData> privatbankArchiveData = restTemplate.getForEntity(privatbankArchiveUrl + "?" + "json" + "&date=" + date, PrivatbankArchiveApiData.class);
        for(Course course : privatbankArchiveData.getBody().getCourses()) {
            if(course.getCurrency() == null) continue;
            try {
                if (course.getCurrency().equals(currency)) {
                    course.setDate(privatbankArchiveData.getBody().getDate());
                    course.setBank("Privatbank");
                    logger.debug("execute getArchiveCourse method with " + course);
                    return CompletableFuture.completedFuture(course);
                }
            } catch (NullPointerException e) {
                logger.warn("NullPointerException");
            }
        }
        logger.debug("execute getArchiveCourse method with NULL");
        return null;
    }
}