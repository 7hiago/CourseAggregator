package com.laba3.utils;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DateValidation {
    private static final Logger logger = LoggerFactory.getLogger(DateValidation.class);

    public String validate(String inputDate, String requireOutputFormat) throws DatatypeException {
        String date;
        logger.debug("executing validate method and get date: " + inputDate);
        LocalDate localDate = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        if(localDate.isAfter(LocalDate.now()) || localDate.isEqual(LocalDate.now())) {
            throw new DatatypeException();
        }
        date = LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("dd.MM.yyyy")).format(DateTimeFormatter.ofPattern(requireOutputFormat));
        logger.debug("executed validate method and get date: " + date);
        return date;
    }
}
