package com.laba3.services;

import com.laba3.entities.Course;
import com.laba3.utils.CurrencyNamingConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service("monobank")
public class MonoBankApiServiceImpl implements BankApiService{
    private static final Logger logger = LoggerFactory.getLogger(MonoBankApiServiceImpl.class);

    @Value("${monobank.url}")
    private String monobankCurrentUrl;

    private final RestTemplate restTemplate;
    private final CurrencyNamingConverter converter;

    public MonoBankApiServiceImpl(RestTemplate restTemplate, CurrencyNamingConverter converter) {
        this.restTemplate = restTemplate;
        this.converter = converter;
    }

    @Async("asyncCourseExecutor")
    @Override
    public CompletableFuture<Course> getCurrentCourse(String currency) {
        Course outCourse = null;
        ResponseEntity<Course[]> courseList = restTemplate.getForEntity(monobankCurrentUrl, Course[].class);

        LocalDateTime now = LocalDateTime.now();
        ZoneId zone = ZoneId.of("Europe/Kiev");

        for (Course course : Objects.requireNonNull(courseList.getBody())) {
            if (course.getCurrency().equals(converter.convert(currency))) {
                course.setCurrency(currency);
                LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(Integer.parseInt(course.getDate()), 0, zone.getRules().getOffset(now));
                course.setDate(localDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                course.setBank("Monobank");
                logger.debug("execute getCurrentCourse method with " + course);
                outCourse = course;
                break;
            }
        }
        if(outCourse == null) {
            return null;
        }
        return CompletableFuture.completedFuture(outCourse);
    }

    @Override
    public CompletableFuture<Course> getArchiveCourse(String date, String currency) {
        return null;
    }
}
