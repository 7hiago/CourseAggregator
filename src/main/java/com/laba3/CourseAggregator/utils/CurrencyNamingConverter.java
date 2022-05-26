package com.laba3.CourseAggregator.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CurrencyNamingConverter {
    private Map<String, String> namingList = new HashMap<>();

    public CurrencyNamingConverter() {
        fillList();
    }

    public String convert(String numberCurrency) {
        return namingList.get(numberCurrency);
    }

    private void fillList() {
        namingList.put("USD", "840");
        namingList.put("EUR", "978");
        namingList.put("PLN", "985");
        namingList.put("GBP", "826");
        namingList.put("CHF", "756");
        namingList.put("CZK", "203");
    }
}
