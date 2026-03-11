package com.portfolio.portal.host.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "config_props")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigProp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Unique configuration key (e.g. "title", "basePath", "sourceUrls"). */
    @Column(name = "prop_key", unique = true, nullable = false)
    private String key;

    /** Stored value. Lists (e.g. sourceUrls) are stored comma-separated. */
    @Column(name = "value", nullable = false)
    private String value;
}
