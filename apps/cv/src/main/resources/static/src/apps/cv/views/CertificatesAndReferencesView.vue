<template>
  <div class="certificates-references-view">
    <wa-spinner v-if="loading" />
    <wa-callout v-else-if="error" variant="danger" open>
      <wa-icon slot="icon" name="exclamation-triangle" />
      {{ error }}
    </wa-callout>
    <template v-else>
      <section>
        <h2 class="cv-section-heading">Certificates</h2>
        <div class="cv-timeline">
          <wa-card v-for="cert in sortedCertificates" :key="cert.id" class="cv-timeline-card">
            <div slot="header" class="cv-card-header">
              <div class="cv-card-title">{{ cert.title }}</div>
              <div class="cv-card-subtitle-row">
                <span class="cv-card-subtitle">{{ cert.issuingOrganization }}</span>
                <span class="cv-subtitle-meta">
                  <wa-icon name="calendar" aria-hidden="true" />
                  <span class="cv-date-range">{{ formatDateRange(cert.startDate, cert.endDate) }}</span>
                  <span v-if="cert.location" class="cv-location">· {{ cert.location }}</span>
                </span>
              </div>
            </div>

            <div v-if="cert.description" class="cv-card-body cv-card-footer">
              {{ cert.description }}
            </div>
          </wa-card>
        </div>
      </section>

      <section>
        <h2 class="cv-section-heading">References</h2>
        <div class="cv-references-grid">
          <wa-card v-for="ref in references" :key="ref.id" class="cv-reference-card">
            <div class="cv-reference-card-header">
              <span class="cv-reference-name">{{ ref.firstName }} {{ ref.lastName }}</span>
              <wa-badge
                v-if="ref.relation"
                :appearance="RELATION_APPEARANCE[ref.relation]"
              >
                {{ RELATION_LABELS[ref.relation] }}
              </wa-badge>
            </div>
            <p class="cv-reference-description">{{ ref.description }}</p>
          </wa-card>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, inject, onMounted } from 'vue';
import type { AxiosInstance } from 'axios';
import type { CertificateResponse, ReferenceResponse } from '../types';
import '@awesome.me/webawesome/dist/styles/webawesome.css';
import '@awesome.me/webawesome/dist/components/card/card.js';
import '@awesome.me/webawesome/dist/components/spinner/spinner.js';
import '@awesome.me/webawesome/dist/components/callout/callout.js';
import '@awesome.me/webawesome/dist/components/icon/icon.js';
import '@awesome.me/webawesome/dist/components/badge/badge.js';
import { allDefined } from '@awesome.me/webawesome/dist/utilities/defined.js';

await allDefined();

const cvApi = inject<AxiosInstance>('cvApi')!;

const loading = ref(true);
const error = ref<string | null>(null);
const certificates = ref<CertificateResponse[]>([]);
const references = ref<ReferenceResponse[]>([]);

const RELATION_LABELS: Record<string, string> = {
  MANAGER: 'Manager',
  COWORKER: 'Coworker',
};

const RELATION_APPEARANCE: Record<string, string> = {
  MANAGER: 'filled',
  COWORKER: 'outlined',
};

const sortedCertificates = computed(() =>
  [...certificates.value].sort(
    (a, b) => new Date(b.startDate).getTime() - new Date(a.startDate).getTime(),
  ),
);

const formatMonthYear = (iso: string) => {
  const [year, month, day] = iso.split('-').map(Number);
  return new Intl.DateTimeFormat('en', { year: 'numeric', month: 'short' }).format(
    new Date(year, month - 1, day),
  );
};

const formatDateRange = (start: string, end: string | null) =>
  `${formatMonthYear(start)} – ${end ? formatMonthYear(end) : 'present'}`;

onMounted(async () => {
  try {
    const [certsRes, refsRes] = await Promise.all([
      cvApi.get<CertificateResponse[]>('/api/cv/certificates'),
      cvApi.get<ReferenceResponse[]>('/api/cv/references'),
    ]);
    certificates.value = certsRes.data;
    references.value = refsRes.data;
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to load certificates and references';
  } finally {
    loading.value = false;
  }
});
</script>
