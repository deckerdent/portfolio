package com.portfolio.cv.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hobby")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hobby extends BaseEntity {

    @Column(nullable = false)
    private String name;
}
