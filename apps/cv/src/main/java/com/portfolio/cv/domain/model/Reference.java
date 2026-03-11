package com.portfolio.cv.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reference")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reference extends BaseEntity {

    public enum ReferenceRelation {
        COWORKER, MANAGER
    }

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "relation")
    @Enumerated(EnumType.STRING)
    private ReferenceRelation relation;
}
