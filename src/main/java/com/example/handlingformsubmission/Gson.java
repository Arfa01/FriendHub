package com.example.handlingformsubmission;

import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Gson {
    public static final com.google.gson.Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
}
