package com.laba3.utils;

import com.laba3.exceptions.DateWrongParametersException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class DateValidation {
    private static final Logger logger = LoggerFactory.getLogger(DateValidation.class);

    public String validate(String inputDate, String requireOutputFormat) {
        String date;
        logger.debug("executing validate method and get date: " + inputDate);
        try {
            LocalDate localDate = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            if (localDate.isAfter(LocalDate.now()) || localDate.isEqual(LocalDate.now())) {
                throw new DateWrongParametersException(inputDate + " wrong date. Date must be before today!");
            }
            date = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("dd.MM.yyyy")).format(DateTimeFormatter.ofPattern(requireOutputFormat));
        } catch (DateTimeParseException e) {
            throw new DateWrongParametersException(inputDate + " date format is not available. Use dd.MM.yyyy format!");
        }
        logger.debug("executed validate method and get date: " + date);
        return date;
    }
}
