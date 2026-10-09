package com.meridian.model;
import java.time.LocalDate;
public record Task(long id,long userId,Long goalId,String title,String description,LocalDate dueDate,String priority,String status) { }
