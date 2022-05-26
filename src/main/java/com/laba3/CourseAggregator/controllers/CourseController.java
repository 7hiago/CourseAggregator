package com.laba3.CourseAggregator.controllers;

import com.laba3.CourseAggregator.entities.Course;
import com.laba3.CourseAggregator.services.CourseService;
import com.laba3.CourseAggregator.services.FeatureCourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CourseController {
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;
    private final FeatureCourseService featureCourseService;

    public CourseController(CourseService courseService, FeatureCourseService featureCourseService) {
        this.courseService = courseService;
        this.featureCourseService = featureCourseService;
    }

    @GetMapping("/nacbank")
    public ResponseEntity<Course> nacbankCurrentCourse(@RequestParam(value = "currency", defaultValue = "USD") String currency) {
        logger.debug("executing current /api/nacbank endpoint");
        return ResponseEntity.ok(courseService.getNacbankCurrentCourse(currency));
    }

    @GetMapping(value = "/nacbank", params = "date")
    public ResponseEntity<Course> nacbankArchiveCourse(@RequestParam(value = "date") String date,
                                                       @RequestParam(value = "currency", defaultValue = "USD") String currency) {
        logger.debug("executing archive /api/nacbank endpoint");
        return ResponseEntity.ok(courseService.getNacbankArchiveCourse(date, currency));
    }

    @GetMapping("/privatbank")
    public ResponseEntity<Course> privatbankCurrentCourse(@RequestParam(value = "currency", defaultValue = "USD") String currency) {
        logger.debug("executing current /api/privatbank endpoint");
        return ResponseEntity.ok(courseService.getPrivatbankCurrentCourse(currency));
    }

    @GetMapping(value = "/privatbank", params = "date")
    public ResponseEntity<Course> privatbankArchiveCourses(@RequestParam(value = "date") String date,
                                                           @RequestParam(value = "currency", defaultValue = "USD") String currency) {
        logger.debug("executing archive /api/privat endpoint");
        return ResponseEntity.ok(courseService.getPrivatbankArchiveCourse(date, currency));
    }

    @GetMapping("/monobank")
    public ResponseEntity<Course> monobankCurrentCourse(@RequestParam(value = "currency", defaultValue = "USD") String currency) {
        logger.debug("executing current /api/monobank endpoint");
        return ResponseEntity.ok(courseService.getMonobankCurrentCourse(currency));
    }

    @GetMapping(value = "/bestCourse")
    public ResponseEntity<Course> bestCourse(@RequestParam(value = "date", defaultValue = "today") String date,
                                             @RequestParam(value = "currency", defaultValue = "USD") String currency) {
        logger.debug("executing /api/features/best endpoint");
        return ResponseEntity.ok(featureCourseService.getBestCourse(date, currency));
    }
}
