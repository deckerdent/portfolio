<template>
  <div class="education-view">
    <wa-spinner v-if="loading" />
    <wa-callout v-else-if="error" variant="danger" open>
      <wa-icon slot="icon" name="exclamation-triangle" />
      {{ error }}
    </wa-callout>
    <template v-else>
      <h2 class="cv-section-heading">Education</h2>
      <div class="cv-timeline">
        <wa-card v-for="edu in sortedEducation" :key="edu.id" class="cv-timeline-card">
          <div slot="header" class="cv-card-header">
            <div class="cv-card-title">{{ edu.title }}</div>
            <div class="cv-card-subtitle-row">
              <span class="cv-card-subtitle">{{ edu.schoolName }}</span>
              <span class="cv-subtitle-meta">
                <wa-icon name="calendar" aria-hidden="true" />
                <span class="cv-date-range">{{ formatDateRange(edu.startDate, edu.endDate) }}</span>
                <span v-if="edu.location" class="cv-location">· {{ edu.location }}</span>
              </span>
            </div>
          </div>

          <div v-if="edu.description" class="cv-card-body cv-card-footer">
            {{ edu.description }}
          </div>
        </wa-card>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, inject, onMounted } from 'vue';
import type { AxiosInstance } from 'axios';
import type { EducationResponse } from '../types';
import '@awesome.me/webawesome/dist/styles/webawesome.css';
import '@awesome.me/webawesome/dist/components/card/card.js';
import '@awesome.me/webawesome/dist/components/spinner/spinner.js';
import '@awesome.me/webawesome/dist/components/callout/callout.js';
import '@awesome.me/webawesome/dist/components/icon/icon.js';
import { allDefined } from '@awesome.me/webawesome/dist/utilities/defined.js';

await allDefined();

const cvApi = inject<AxiosInstance>('cvApi')!;

const loading = ref(true);
const error = ref<string | null>(null);
const education = ref<EducationResponse[]>([]);

const sortedEducation = computed(() => {
  return [...education.value].sort((a, b) => {
    return new Date(b.startDate).getTime() - new Date(a.startDate).getTime();
  });
});

const formatMonthYear = (iso: string) => {
  const [year, month, day] = iso.split('-').map(Number);
  return new Intl.DateTimeFormat('en', { year: 'numeric', month: 'short' }).format(new Date(year, month - 1, day));
};

const formatDateRange = (start: string, end: string | null) =>
  `${formatMonthYear(start)} – ${end ? formatMonthYear(end) : 'present'}`;

onMounted(async () => {
  try {
    const response = await cvApi.get<EducationResponse[]>('/api/cv/education');
    education.value = response.data;
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to load education';
  } finally {
    loading.value = false;
  }
});
</script>
