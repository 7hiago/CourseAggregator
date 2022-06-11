package com.laba3.services;

import com.laba3.entities.Course;

import java.util.concurrent.CompletableFuture;

public interface BankApiService {

    CompletableFuture<Course> getCurrentCourse(String currency);

    CompletableFuture<Course> getArchiveCourse(String date, String currency);
}
