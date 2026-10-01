package com.portfolio.cv.controller;

import com.portfolio.cv.api.CvApi;
import com.portfolio.cv.domain.mapper.*;
import com.portfolio.cv.domain.service.*;
import com.portfolio.cv.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CvController implements CvApi {

        private final GeneralInfoService generalInfoService;
        private final GeneralInfoMapper generalInfoMapper;

        private final ExperienceService experienceService;
        private final ExperienceMapper experienceMapper;

        private final EducationService educationService;
        private final EducationMapper educationMapper;

        private final SkillService skillService;
        private final SkillMapper skillMapper;

        private final CompetenceService competenceService;
        private final CompetenceMapper competenceMapper;

        private final LanguageService languageService;
        private final LanguageMapper languageMapper;

        private final HobbyService hobbyService;
        private final HobbyMapper hobbyMapper;

        private final ReferenceService referenceService;
        private final ReferenceMapper referenceMapper;

        private final CertificateService certificateService;
        private final CertificateMapper certificateMapper;

        // ── GeneralInfo ─────────────────────────────────────────────────────────

        @Override
        public Mono<ResponseEntity<GeneralInfoResponse>> getGeneralInfo(ServerWebExchange exchange) {
                return generalInfoService.findOrEmpty()
                                .map(opt -> opt.map(generalInfoMapper::toDto)
                                                .map(ResponseEntity::ok)
                                                .orElse(ResponseEntity.notFound().build()));
        }

        @Override
        public Mono<ResponseEntity<GeneralInfoResponse>> upsertGeneralInfo(
                        Mono<GeneralInfoRequest> generalInfoRequest, ServerWebExchange exchange) {
                return generalInfoRequest
                                .map(generalInfoMapper::toEntity)
                                .flatMap(generalInfoService::upsert)
                                .map(generalInfoMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        // ── Experience ──────────────────────────────────────────────────────────

        @Override
        public Mono<ResponseEntity<Flux<ExperienceResponse>>> getExperiences(ServerWebExchange exchange) {
                return experienceService.findAll()
                                .map(list -> ResponseEntity.ok(Flux.fromIterable(experienceMapper.toDtoList(list))));
        }

        @Override
        public Mono<ResponseEntity<ExperienceResponse>> getExperienceById(UUID id, ServerWebExchange exchange) {
                return experienceService.findById(id)
                                .map(experienceMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<ExperienceResponse>> createExperience(
                        Mono<ExperienceRequest> experienceRequest, ServerWebExchange exchange) {
                return experienceRequest
                                .map(experienceMapper::toEntity)
                                .flatMap(experienceService::create)
                                .map(experienceMapper::toDto)
                                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
        }

        @Override
        public Mono<ResponseEntity<ExperienceResponse>> updateExperience(
                        UUID id, Mono<ExperienceRequest> experienceRequest, ServerWebExchange exchange) {
                return experienceRequest
                                .map(experienceMapper::toEntity)
                                .flatMap(entity -> experienceService.update(id, entity))
                                .map(experienceMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<Void>> deleteExperience(UUID id, ServerWebExchange exchange) {
                return experienceService.delete(id)
                                .thenReturn(ResponseEntity.noContent().<Void>build());
        }

        // ── Education ───────────────────────────────────────────────────────────

        @Override
        public Mono<ResponseEntity<Flux<EducationResponse>>> getEducation(ServerWebExchange exchange) {
                return educationService.findAll()
                                .map(list -> ResponseEntity.ok(Flux.fromIterable(educationMapper.toDtoList(list))));
        }

        @Override
        public Mono<ResponseEntity<EducationResponse>> getEducationById(UUID id, ServerWebExchange exchange) {
                return educationService.findById(id)
                                .map(educationMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<EducationResponse>> createEducation(
                        Mono<EducationRequest> educationRequest, ServerWebExchange exchange) {
                return educationRequest
                                .map(educationMapper::toEntity)
                                .flatMap(educationService::create)
                                .map(educationMapper::toDto)
                                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
        }

        @Override
        public Mono<ResponseEntity<EducationResponse>> updateEducation(
                        UUID id, Mono<EducationRequest> educationRequest, ServerWebExchange exchange) {
                return educationRequest
                                .map(educationMapper::toEntity)
                                .flatMap(entity -> educationService.update(id, entity))
                                .map(educationMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<Void>> deleteEducation(UUID id, ServerWebExchange exchange) {
                return educationService.delete(id)
                                .thenReturn(ResponseEntity.noContent().<Void>build());
        }

        // ── Skills ──────────────────────────────────────────────────────────────

        @Override
        public Mono<ResponseEntity<Flux<SkillResponse>>> getSkills(ServerWebExchange exchange) {
                return skillService.findAll()
                                .map(list -> ResponseEntity.ok(Flux.fromIterable(skillMapper.toDtoList(list))));
        }

        @Override
        public Mono<ResponseEntity<SkillResponse>> getSkillById(UUID id, ServerWebExchange exchange) {
                return skillService.findById(id)
                                .map(skillMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<SkillResponse>> createSkill(
                        Mono<SkillRequest> skillRequest, ServerWebExchange exchange) {
                return skillRequest
                                .map(skillMapper::toEntity)
                                .flatMap(skillService::create)
                                .map(skillMapper::toDto)
                                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
        }

        @Override
        public Mono<ResponseEntity<SkillResponse>> updateSkill(
                        UUID id, Mono<SkillRequest> skillRequest, ServerWebExchange exchange) {
                return skillRequest
                                .map(skillMapper::toEntity)
                                .flatMap(entity -> skillService.update(id, entity))
                                .map(skillMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<Void>> deleteSkill(UUID id, ServerWebExchange exchange) {
                return skillService.delete(id)
                                .thenReturn(ResponseEntity.noContent().<Void>build());
        }

        // ── Competences ─────────────────────────────────────────────────────────

        @Override
        public Mono<ResponseEntity<Flux<CompetenceResponse>>> getCompetences(ServerWebExchange exchange) {
                return competenceService.findAll()
                                .map(list -> ResponseEntity.ok(Flux.fromIterable(competenceMapper.toDtoList(list))));
        }

        @Override
        public Mono<ResponseEntity<CompetenceResponse>> getCompetenceById(UUID id, ServerWebExchange exchange) {
                return competenceService.findById(id)
                                .map(competenceMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<CompetenceResponse>> createCompetence(
                        Mono<CompetenceRequest> competenceRequest, ServerWebExchange exchange) {
                return competenceRequest
                                .map(competenceMapper::toEntity)
                                .flatMap(competenceService::create)
                                .map(competenceMapper::toDto)
                                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
        }

        @Override
        public Mono<ResponseEntity<CompetenceResponse>> updateCompetence(
                        UUID id, Mono<CompetenceRequest> competenceRequest, ServerWebExchange exchange) {
                return competenceRequest
                                .map(competenceMapper::toEntity)
                                .flatMap(entity -> competenceService.update(id, entity))
                                .map(competenceMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<Void>> deleteCompetence(UUID id, ServerWebExchange exchange) {
                return competenceService.delete(id)
                                .thenReturn(ResponseEntity.noContent().<Void>build());
        }

        // ── Languages ───────────────────────────────────────────────────────────

        @Override
        public Mono<ResponseEntity<Flux<LanguageResponse>>> getLanguages(ServerWebExchange exchange) {
                return languageService.findAll()
                                .map(list -> ResponseEntity.ok(Flux.fromIterable(languageMapper.toDtoList(list))));
        }

        @Override
        public Mono<ResponseEntity<LanguageResponse>> getLanguageById(UUID id, ServerWebExchange exchange) {
                return languageService.findById(id)
                                .map(languageMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<LanguageResponse>> createLanguage(
                        Mono<LanguageRequest> languageRequest, ServerWebExchange exchange) {
                return languageRequest
                                .map(languageMapper::toEntity)
                                .flatMap(languageService::create)
                                .map(languageMapper::toDto)
                                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
        }

        @Override
        public Mono<ResponseEntity<LanguageResponse>> updateLanguage(
                        UUID id, Mono<LanguageRequest> languageRequest, ServerWebExchange exchange) {
                return languageRequest
                                .map(languageMapper::toEntity)
                                .flatMap(entity -> languageService.update(id, entity))
                                .map(languageMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<Void>> deleteLanguage(UUID id, ServerWebExchange exchange) {
                return languageService.delete(id)
                                .thenReturn(ResponseEntity.noContent().<Void>build());
        }

        // ── Hobbies ─────────────────────────────────────────────────────────────

        @Override
        public Mono<ResponseEntity<Flux<HobbyResponse>>> getHobbies(ServerWebExchange exchange) {
                return hobbyService.findAll()
                                .map(list -> ResponseEntity.ok(Flux.fromIterable(hobbyMapper.toDtoList(list))));
        }

        @Override
        public Mono<ResponseEntity<HobbyResponse>> getHobbyById(UUID id, ServerWebExchange exchange) {
                return hobbyService.findById(id)
                                .map(hobbyMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<HobbyResponse>> createHobby(
                        Mono<HobbyRequest> hobbyRequest, ServerWebExchange exchange) {
                return hobbyRequest
                                .map(hobbyMapper::toEntity)
                                .flatMap(hobbyService::create)
                                .map(hobbyMapper::toDto)
                                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
        }

        @Override
        public Mono<ResponseEntity<HobbyResponse>> updateHobby(
                        UUID id, Mono<HobbyRequest> hobbyRequest, ServerWebExchange exchange) {
                return hobbyRequest
                                .map(hobbyMapper::toEntity)
                                .flatMap(entity -> hobbyService.update(id, entity))
                                .map(hobbyMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<Void>> deleteHobby(UUID id, ServerWebExchange exchange) {
                return hobbyService.delete(id)
                                .thenReturn(ResponseEntity.noContent().<Void>build());
        }

        // ── References ───────────────────────────────────────────────────────────

        @Override
        public Mono<ResponseEntity<Flux<ReferenceResponse>>> getReferences(ServerWebExchange exchange) {
                return referenceService.findAll()
                                .map(list -> ResponseEntity.ok(Flux.fromIterable(referenceMapper.toDtoList(list))));
        }

        @Override
        public Mono<ResponseEntity<ReferenceResponse>> getReferenceById(UUID id, ServerWebExchange exchange) {
                return referenceService.findById(id)
                                .map(referenceMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<ReferenceResponse>> createReference(
                        Mono<ReferenceRequest> referenceRequest, ServerWebExchange exchange) {
                return referenceRequest
                                .map(referenceMapper::toEntity)
                                .flatMap(referenceService::create)
                                .map(referenceMapper::toDto)
                                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
        }

        @Override
        public Mono<ResponseEntity<ReferenceResponse>> updateReference(
                        UUID id, Mono<ReferenceRequest> referenceRequest, ServerWebExchange exchange) {
                return referenceRequest
                                .map(referenceMapper::toEntity)
                                .flatMap(entity -> referenceService.update(id, entity))
                                .map(referenceMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<Void>> deleteReference(UUID id, ServerWebExchange exchange) {
                return referenceService.delete(id)
                                .thenReturn(ResponseEntity.noContent().<Void>build());
        }

        // ── Certificates ───────────────────────────────────────────────────────

        @Override
        public Mono<ResponseEntity<Flux<CertificateResponse>>> getCertificates(ServerWebExchange exchange) {
                return certificateService.findAll()
                                .map(list -> ResponseEntity.ok(Flux.fromIterable(certificateMapper.toDtoList(list))));
        }

        @Override
        public Mono<ResponseEntity<CertificateResponse>> getCertificateById(UUID id, ServerWebExchange exchange) {
                return certificateService.findById(id)
                                .map(certificateMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<CertificateResponse>> createCertificate(
                        Mono<CertificateRequest> certificateRequest, ServerWebExchange exchange) {
                return certificateRequest
                                .map(certificateMapper::toEntity)
                                .flatMap(certificateService::create)
                                .map(certificateMapper::toDto)
                                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
        }

        @Override
        public Mono<ResponseEntity<CertificateResponse>> updateCertificate(
                        UUID id, Mono<CertificateRequest> certificateRequest, ServerWebExchange exchange) {
                return certificateRequest
                                .map(certificateMapper::toEntity)
                                .flatMap(entity -> certificateService.update(id, entity))
                                .map(certificateMapper::toDto)
                                .map(ResponseEntity::ok);
        }

        @Override
        public Mono<ResponseEntity<Void>> deleteCertificate(UUID id, ServerWebExchange exchange) {
                return certificateService.delete(id)
                                .thenReturn(ResponseEntity.noContent().<Void>build());
        }
}
