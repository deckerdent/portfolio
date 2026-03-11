package com.portfolio.cv.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class TimelineEntry extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;
}
