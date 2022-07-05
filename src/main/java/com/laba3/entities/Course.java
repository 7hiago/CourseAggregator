package com.laba3.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Course implements Comparable<Course> {

    @JsonAlias({"rate", "rateSell", "saleRate", "sale"})
    private float course;

    @JsonAlias({"exchangedate", "date"})
    private String date;

    @JsonAlias({"cc", "currencyCodeA", "currency", "ccy"})
    private String currency;

    private String bank;

    public Course() {
    }

    public float getCourse() {
        return course;
    }

    public void setCourse(float course) {
        this.course = course;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    @Override
    public String toString() {
        return "Course of " + currency + " in " + bank + " on " + date + " is " + course;
    }

    @Override
    public int compareTo(Course o) {
        return Float.compare(getCourse(), o.getCourse());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course1 = (Course) o;
        return Float.compare(course1.getCourse(), getCourse()) == 0 && getDate().equals(course1.getDate()) && getCurrency().equals(course1.getCurrency()) && getBank().equals(course1.getBank());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCourse(), getDate(), getCurrency(), getBank());
    }
}