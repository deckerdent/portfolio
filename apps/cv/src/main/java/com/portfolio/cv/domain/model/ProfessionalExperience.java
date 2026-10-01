package com.portfolio.cv.domain.model;

import com.portfolio.cv.domain.model.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "skills", columnDefinition = "TEXT")
    private List<String> skills = new ArrayList<>();

    @Column(name = "highlights", columnDefinition = "TEXT")
    private String highlights;
}
