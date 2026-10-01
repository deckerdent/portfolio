package com.portfolio.portal.host;

import com.portfolio.portal.host.model.ConfigProp;
import com.portfolio.portal.host.model.PortalHost;
import com.portfolio.portal.host.repository.ConfigPropRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HostConfigServiceTest {

        @Mock
        private ConfigPropRepository repository;

        @Mock
        private PortalHost host;

        @InjectMocks
        private HostConfigServiceImpl service;

        // ── isInitialized ────────────────────────────────────────────────────────

        @Test
        void isInitialized_returnsFalse_whenTableIsEmpty() {
                when(repository.findAll()).thenReturn(List.of());

                StepVerifier.create(service.isInitialized())
                                .expectNext(false)
                                .verifyComplete();
        }

        @Test
        void isInitialized_returnsFalse_whenOnlyTitleIsPresent() {
                when(repository.findAll()).thenReturn(List.of(
                                ConfigProp.builder().key("title").value("Portal").build()));

                StepVerifier.create(service.isInitialized())
                                .expectNext(false)
                                .verifyComplete();
        }

        @Test
        void isInitialized_returnsFalse_whenOnlyBasePathIsPresent() {
                when(repository.findAll()).thenReturn(List.of(
                                ConfigProp.builder().key("basePath").value("/").build()));

                StepVerifier.create(service.isInitialized())
                                .expectNext(false)
                                .verifyComplete();
        }

        @Test
        void isInitialized_returnsTrue_whenBothRequiredKeysAreSet() {
                when(repository.findAll()).thenReturn(List.of(
                                ConfigProp.builder().key("title").value("Portal").build(),
                                ConfigProp.builder().key("basePath").value("/").build()));

                StepVerifier.create(service.isInitialized())
                                .expectNext(true)
                                .verifyComplete();
        }

        @Test
        void isInitialized_returnsFalse_whenRequiredValueIsBlank() {
                when(repository.findAll()).thenReturn(List.of(
                                ConfigProp.builder().key("title").value("  ").build(),
                                ConfigProp.builder().key("basePath").value("/").build()));

                StepVerifier.create(service.isInitialized())
                                .expectNext(false)
                                .verifyComplete();
        }

        // ── upsert ───────────────────────────────────────────────────────────────

        @Test
        void upsert_createsNewProp_whenKeyIsAbsent() {
                UUID id = UUID.randomUUID();
                ConfigProp saved = ConfigProp.builder().id(id).key("title").value("My Portal").build();
                when(repository.findByKey("title")).thenReturn(Optional.empty());
                when(repository.save(any())).thenReturn(saved);
                when(repository.findAll()).thenReturn(List.of(saved));

                StepVerifier.create(service.upsert("title", "My Portal"))
                                .assertNext(prop -> {
                                        assertThat(prop.getKey()).isEqualTo("title");
                                        assertThat(prop.getValue()).isEqualTo("My Portal");
                                })
                                .verifyComplete();

                verify(repository).save(argThat(p -> "title".equals(p.getKey()) && "My Portal".equals(p.getValue())));
        }

        @Test
        void upsert_updatesExistingProp_whenKeyIsPresent() {
                UUID id = UUID.randomUUID();
                ConfigProp existing = ConfigProp.builder().id(id).key("title").value("Old").build();
                ConfigProp saved = ConfigProp.builder().id(id).key("title").value("New").build();
                when(repository.findByKey("title")).thenReturn(Optional.of(existing));
                when(repository.save(any())).thenReturn(saved);
                when(repository.findAll()).thenReturn(List.of(saved));

                StepVerifier.create(service.upsert("title", "New"))
                                .assertNext(prop -> assertThat(prop.getValue()).isEqualTo("New"))
                                .verifyComplete();
        }

        // ── update ───────────────────────────────────────────────────────────────

        @Test
        void update_returnsUpdatedProp_whenKeyExists() {
                UUID id = UUID.randomUUID();
                ConfigProp existing = ConfigProp.builder().id(id).key("basePath").value("/old").build();
                ConfigProp saved = ConfigProp.builder().id(id).key("basePath").value("/new").build();
                when(repository.findByKey("basePath")).thenReturn(Optional.of(existing));
                when(repository.save(any())).thenReturn(saved);
                when(repository.findAll()).thenReturn(List.of(saved));

                StepVerifier.create(service.update("basePath", "/new"))
                                .assertNext(prop -> assertThat(prop.getValue()).isEqualTo("/new"))
                                .verifyComplete();
        }

        @Test
        void update_returnsErrorSignal_whenKeyNotFound() {
                when(repository.findByKey("nonexistent")).thenReturn(Optional.empty());

                StepVerifier.create(service.update("nonexistent", "value"))
                                .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                                                e.getMessage().contains("nonexistent"))
                                .verify();
        }

        // ── delete ───────────────────────────────────────────────────────────────

        @Test
        void delete_completesSuccessfully_whenKeyExists() {
                when(repository.existsByKey("title")).thenReturn(true);
                when(repository.findAll()).thenReturn(List.of());

                StepVerifier.create(service.delete("title"))
                                .verifyComplete();

                verify(repository).deleteByKey("title");
        }

        @Test
        void delete_returnsErrorSignal_whenKeyNotFound() {
                when(repository.existsByKey("missing")).thenReturn(false);

                StepVerifier.create(service.delete("missing"))
                                .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                                                e.getMessage().contains("missing"))
                                .verify();
        }
}
