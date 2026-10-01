<template>
  <div class="timeline-view">
    <wa-spinner v-if="loading" />
    <wa-callout v-else-if="error" variant="danger" open>
      <wa-icon slot="icon" name="exclamation-triangle" />
      {{ error }}
    </wa-callout>

    <template v-else>
      <h2 class="cv-section-heading">Timeline</h2>

      <div v-if="timelineRows.length === 0" class="cv-timeline-empty">
        No timeline entries available.
      </div>

      <div v-else class="cv-unified-timeline">
        <article
          v-for="(row, rowIndex) in timelineRows"
          :key="row.id"
          class="cv-unified-row"
          :class="{
            'cv-unified-row--first': rowIndex === 0,
            'cv-unified-row--last': rowIndex === timelineRows.length - 1,
          }"
        >
          <div class="cv-unified-column cv-unified-column--left">
            <wa-card
              v-for="entry in row.left"
              :key="entry.id"
              class="cv-timeline-card cv-unified-card"
            >
              <div slot="header" class="cv-card-header">
                <div class="cv-card-meta-row">
                  <wa-badge appearance="filled" variant="warning">Work</wa-badge>
                </div>
                <div class="cv-card-title">{{ getTitle(entry) }}</div>
                <div class="cv-card-subtitle-row">
                  <span class="cv-card-subtitle">{{ getSubtitle(entry) }}</span>
                  <span class="cv-subtitle-meta">
                    <wa-icon name="calendar" aria-hidden="true" />
                    <span class="cv-date-range">{{ formatDateRange(entry.startDate, entry.endDate) }}</span>
                    <span v-if="getLocation(entry)" class="cv-location">· {{ getLocation(entry) }}</span>
                  </span>
                </div>
              </div>

              <div v-if="getDescription(entry)" class="cv-card-body cv-card-footer">
                {{ getDescription(entry) }}
              </div>

              <div v-if="getSkills(entry).length > 0" class="cv-experience-skills">
                <wa-badge
                  v-for="skill in getSkills(entry)"
                  :key="`${entry.id}-${skill}`"
                  appearance="outlined"
                  variant="brand"
                  pill
                >
                  {{ skill }}
                </wa-badge>
              </div>

              <div v-if="getHighlights(entry)" class="cv-experience-highlights">
                <span class="cv-experience-highlights-label">Highlight</span>
                <span>{{ getHighlights(entry) }}</span>
              </div>
            </wa-card>
          </div>

          <div class="cv-unified-track" aria-hidden="true">
            <span class="cv-unified-dot" />
          </div>

          <div class="cv-unified-column cv-unified-column--right">
            <wa-card
              v-for="entry in row.right"
              :key="entry.id"
              class="cv-timeline-card cv-unified-card"
            >
              <div slot="header" class="cv-card-header">
                <div class="cv-card-meta-row">
                  <wa-badge
                    :appearance="entry.kind === 'certificate' ? 'outlined' : 'filled'"
                    :variant="entry.kind === 'certificate' ? 'brand' : 'success'"
                  >
                    {{ entry.kind === 'certificate' ? 'Certificate' : 'Education' }}
                  </wa-badge>
                </div>
                <div class="cv-card-title">{{ getTitle(entry) }}</div>
                <div class="cv-card-subtitle-row">
                  <span class="cv-card-subtitle">{{ getSubtitle(entry) }}</span>
                  <span class="cv-subtitle-meta">
                    <wa-icon name="calendar" aria-hidden="true" />
                    <span class="cv-date-range">{{ formatDateRange(entry.startDate, entry.endDate) }}</span>
                    <span v-if="getLocation(entry)" class="cv-location">· {{ getLocation(entry) }}</span>
                  </span>
                </div>
              </div>

              <div v-if="getDescription(entry)" class="cv-card-body cv-card-footer">
                {{ getDescription(entry) }}
              </div>

              <div v-if="getSkills(entry).length > 0" class="cv-experience-skills">
                <wa-badge
                  v-for="skill in getSkills(entry)"
                  :key="`${entry.id}-${skill}`"
                  appearance="outlined"
                  variant="brand"
                  pill
                >
                  {{ skill }}
                </wa-badge>
              </div>

              <div v-if="getHighlights(entry)" class="cv-experience-highlights">
                <span class="cv-experience-highlights-label">Highlight</span>
                <span>{{ getHighlights(entry) }}</span>
              </div>
            </wa-card>
          </div>
        </article>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, inject, onMounted, ref } from 'vue';
import type { AxiosInstance } from 'axios';
import type { CertificateResponse, EducationResponse, ExperienceResponse } from '../types';
import type { NormalizedTimelineEntry } from '../utils/timeline';
import { getExperienceHighlight, getExperienceSkills } from '../utils/experienceDisplay';
import { buildTimelineRows } from '../utils/timeline';
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
const experiences = ref<ExperienceResponse[]>([]);
const education = ref<EducationResponse[]>([]);
const certificates = ref<CertificateResponse[]>([]);

const timelineRows = computed(() =>
  buildTimelineRows(experiences.value, education.value, certificates.value),
);

const formatMonthYear = (iso: string) => {
  const [yearString = '', monthString = '', dayString = '01'] = iso.split('-');
  const parsedYear = Number.parseInt(yearString, 10);
  const parsedMonth = Number.parseInt(monthString || '1', 10);
  const parsedDay = Number.parseInt(dayString || '1', 10);
  const year = Number.isFinite(parsedYear) ? parsedYear : 1970;
  const month = Number.isFinite(parsedMonth) ? Math.min(12, Math.max(1, parsedMonth)) : 1;
  const day = Number.isFinite(parsedDay) ? Math.min(31, Math.max(1, parsedDay)) : 1;

  return new Intl.DateTimeFormat('en', { year: 'numeric', month: 'short' }).format(
    new Date(year, month - 1, day),
  );
};

const formatDateRange = (start: string, end: string | null) =>
  `${formatMonthYear(start)} – ${end ? formatMonthYear(end) : 'present'}`;

const getTitle = (entry: NormalizedTimelineEntry) => entry.payload.title;

const getSubtitle = (entry: NormalizedTimelineEntry) => {
  if (entry.kind === 'experience') {
    return (entry.payload as ExperienceResponse).companyName;
  }

  if (entry.kind === 'education') {
    return (entry.payload as EducationResponse).schoolName;
  }

  return (entry.payload as CertificateResponse).issuingOrganization;
};

const getLocation = (entry: NormalizedTimelineEntry) => entry.payload.location;

const getDescription = (entry: NormalizedTimelineEntry) => entry.payload.description;

const getSkills = (entry: NormalizedTimelineEntry) => {
  if (entry.kind !== 'experience') {
    return [];
  }

  return getExperienceSkills((entry.payload as ExperienceResponse).skills);
};

const getHighlights = (entry: NormalizedTimelineEntry) => {
  if (entry.kind !== 'experience') {
    return null;
  }

  return getExperienceHighlight((entry.payload as ExperienceResponse).highlights);
};

onMounted(async () => {
  try {
    const [experiencesRes, educationRes, certificatesRes] = await Promise.all([
      cvApi.get<ExperienceResponse[]>('/api/cv/experiences'),
      cvApi.get<EducationResponse[]>('/api/cv/education'),
      cvApi.get<CertificateResponse[]>('/api/cv/certificates'),
    ]);

    experiences.value = experiencesRes.data;
    education.value = educationRes.data;
    certificates.value = certificatesRes.data;
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to load timeline entries';
  } finally {
    loading.value = false;
  }
});
</script>
