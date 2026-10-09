package com.meridian.model;
import java.time.LocalDate;
public record Goal(long id,long userId,String title,String description,LocalDate startDate,LocalDate targetDate,String status) { }
