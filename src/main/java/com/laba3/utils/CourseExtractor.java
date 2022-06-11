package com.laba3.utils;

import com.laba3.entities.Course;

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
