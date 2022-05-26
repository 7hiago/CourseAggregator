package com.laba3.CourseAggregator.services;

import com.laba3.CourseAggregator.entities.Course;
import com.laba3.CourseAggregator.utils.CurrencyNamingConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Service("monobank")
public class MonoBankApiServiceImpl implements BankApiService{
    private static final Logger logger = LoggerFactory.getLogger(MonoBankApiServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${monobank.url}")
    private String monobankCurrentUrl;

    @Autowired
    private CurrencyNamingConverter converter;

    @Async("asyncCourseExecutor")
    @Override
    public CompletableFuture<Course> getCurrentCourse(String currency) {
        ResponseEntity<Course[]> courseList = restTemplate.getForEntity(monobankCurrentUrl, Course[].class);
        LocalDateTime now = LocalDateTime.now();
        ZoneId zone = ZoneId.of("Europe/Kiev");
        for(Course course : courseList.getBody()) {
            if(course.getCurrency().equals(converter.convert(currency))) {
                course.setCurrency(currency);
                LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(Integer.valueOf(course.getDate()), 0, zone.getRules().getOffset(now));
                course.setDate(localDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                course.setBank("Monobank");
                logger.debug("execute getCurrentCourse method with " + course);
                return CompletableFuture.completedFuture(course);
            }
        }
        logger.debug("execute getCurrentCourse method with NULL");
        return null;
    }

    @Override
    public CompletableFuture<Course> getArchiveCourse(String date, String currency) {
        return null;
    }
}
