package com.portfolio.cv.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "professional_experience")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalExperience extends TimelineEntry {

    @Column(name = "company_name", nullable = false)
    private String companyName;
}
