package com.laba3.CourseAggregator.utils;

import com.laba3.CourseAggregator.entities.Course;

public class CourseExtractor {
    public static Course extract(Course[] courseList, String requiredCurrency) {
        for (Course course : courseList) {
            if (course.getCurrency() == null) continue;
            if(course.getCurrency().equals(requiredCurrency)) {
                return course;
            }
        }
        return null;
    }
}
