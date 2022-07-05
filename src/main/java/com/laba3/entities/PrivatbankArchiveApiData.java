package com.laba3.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivatbankArchiveApiData {

    @JsonProperty("date")
    private String date;

    @JsonAlias({"exchangeRate"})
    private List<Course> courses;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        StringBuilder coursesInString = new StringBuilder();
        courses.forEach(course -> coursesInString.append(course.toString()).append('\n'));

        return "PrivatbankArchiveApiData: " + '\n' +
                "date:'" + date + '\'' + ',' + '\n' +
                "courses:" + '\n' +
                 coursesInString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrivatbankArchiveApiData)) return false;
        PrivatbankArchiveApiData that = (PrivatbankArchiveApiData) o;
        return getDate().equals(that.getDate()) && getCourses().equals(that.getCourses());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getCourses());
    }
}
