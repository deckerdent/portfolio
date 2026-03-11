package com.portfolio.cv.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "certificate")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Certificate extends TimelineEntry {

    @Column(name = "issuing_organization", nullable = false)
    private String issuingOrganization;
}
