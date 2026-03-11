package com.portfolio.cv.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "education")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Education extends TimelineEntry {

    @Column(name = "school_name", nullable = false)
    private String schoolName;
}
