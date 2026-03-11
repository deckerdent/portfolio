package com.portfolio.cv.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "competence")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Competence extends BaseEntity {

    @Column(nullable = false)
    private String description;
}
