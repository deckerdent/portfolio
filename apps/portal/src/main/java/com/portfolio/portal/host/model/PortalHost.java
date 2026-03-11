package com.portfolio.portal.host.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton bean holding the resolved host configuration.
 *
 * <p>
 * Properties are populated at application startup (and after each write) by
 * {@code HostConfigService.refresh()}. Typed fields mirror the frontend
 * {@code HostConfig}
 * interface; {@code configProps} exposes the full raw key-value store.
 * </p>
 *
 * <p>
 * Known keys and their defaults:
 * </p>
 * <ul>
 * <li>{@code title} — empty string</li>
 * <li>{@code basePath} — {@code "/"}</li>
 * <li>{@code sourceUrls} — empty list (stored comma-separated)</li>
 * <li>{@code appsUrl} — {@code "/apps.json"}</li>
 * </ul>
 */
@Component
@Getter
@Setter
public class PortalHost {

    private String title = "";
    private String basePath = "/";
    private List<String> sourceUrls = new ArrayList<>();
    private String appsUrl = "/apps.json";
    private Map<String, String> configProps = new HashMap<>();

    /**
     * Apply a fresh snapshot of all stored config props.
     * Sets each typed field from the map and stores the full raw map.
     */
    public void applyProps(Map<String, String> props) {
        this.configProps = Collections.unmodifiableMap(new HashMap<>(props));
        this.title = props.getOrDefault("title", "");
        this.basePath = props.getOrDefault("basePath", "/");

        String raw = props.get("sourceUrls");
        if (raw != null && !raw.isBlank()) {
            this.sourceUrls = List.of(raw.split(","))
                    .stream()
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
        } else {
            this.sourceUrls = new ArrayList<>();
        }

        this.appsUrl = props.getOrDefault("appsUrl", "/apps.json");
    }
}
