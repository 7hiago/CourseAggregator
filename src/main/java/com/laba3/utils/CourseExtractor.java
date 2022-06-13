package com.laba3.utils;

import com.laba3.entities.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseExtractor {
    public Course extract(Course[] courseList, String requiredCurrency) {
        for (Course course : courseList) {
            if (course.getCurrency() == null) continue;
            if(course.getCurrency().equals(requiredCurrency)) {
                return course;
            }
        }
        return null;
    }
}
