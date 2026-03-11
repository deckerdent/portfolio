package com.portfolio.cv.loader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.cv.domain.model.*;
import com.portfolio.cv.domain.repository.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@ConditionalOnProperty(name = "cv.demo-data.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class CvDataLoader {

    private static final String DATA_PATH = "cv.json";

    private final ObjectMapper objectMapper;
    private final GeneralInfoRepository generalInfoRepository;
    private final ProfessionalExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final CompetenceRepository competenceRepository;
    private final LanguageRepository languageRepository;
    private final HobbyRepository hobbyRepository;
    private final ReferenceRepository referenceRepository;
    private final CertificateRepository certificateRepository;

    /** Thin wrapper so Jackson knows the top-level JSON structure. */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CvData {
        private GeneralInfo generalInfo;
        private List<ProfessionalExperience> experiences;
        private List<Education> education;
        private List<Skill> skills;
        private List<Competence> competences;
        private List<Language> languages;
        private List<Hobby> hobbies;
        private List<Reference> references;
        private List<Certificate> certificates;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (generalInfoRepository.count() > 0) {
            log.info("[CvDataLoader] Demo data already present, skipping");
            return;
        }

        CvData data = loadData();
        if (data == null)
            return;

        if (data.getGeneralInfo() != null)
            generalInfoRepository.save(data.getGeneralInfo());
        if (data.getExperiences() != null)
            experienceRepository.saveAll(data.getExperiences());
        if (data.getEducation() != null)
            educationRepository.saveAll(data.getEducation());
        if (data.getSkills() != null)
            skillRepository.saveAll(data.getSkills());
        if (data.getCompetences() != null)
            competenceRepository.saveAll(data.getCompetences());
        if (data.getLanguages() != null)
            languageRepository.saveAll(data.getLanguages());
        if (data.getHobbies() != null)
            hobbyRepository.saveAll(data.getHobbies());
        if (data.getReferences() != null)
            referenceRepository.saveAll(data.getReferences());
        if (data.getCertificates() != null)
            certificateRepository.saveAll(data.getCertificates());

        log.info("[CvDataLoader] Demo data loaded successfully");
    }

    private CvData loadData() {
        Resource fsResource = new FileSystemResource(DATA_PATH);
        if (fsResource.exists()) {
            log.info("[CvDataLoader] Loading demo data from file system: {}", fsResource.getDescription());
            return parse(fsResource);
        }

        Resource cpResource = new ClassPathResource("static/cv.json");
        if (cpResource.exists()) {
            log.info("[CvDataLoader] Loading demo data from classpath: {}", cpResource.getDescription());
            return parse(cpResource);
        }

        log.warn("[CvDataLoader] cv.json not found — no demo data loaded");
        return null;
    }

    private CvData parse(Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            return objectMapper.readValue(is, CvData.class);
        } catch (IOException e) {
            log.error("[CvDataLoader] Failed to parse cv.json: {}", e.getMessage(), e);
            return null;
        }
    }
}
