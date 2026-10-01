package com.portfolio.cv.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "language")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Language extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer level;
}
