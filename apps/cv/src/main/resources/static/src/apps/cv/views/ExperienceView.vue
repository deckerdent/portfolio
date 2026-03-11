<template>
  <div class="experience-view">
    <wa-spinner v-if="loading" />
    <wa-callout v-else-if="error" variant="danger" open>
      <wa-icon slot="icon" name="exclamation-triangle" />
      {{ error }}
    </wa-callout>
    <template v-else>
      <h2 class="cv-section-heading">Experience</h2>
      <div class="cv-timeline">
        <wa-card v-for="exp in sortedExperiences" :key="exp.id" class="cv-timeline-card">
          <div slot="header" class="cv-card-header">
            <div class="cv-card-title">{{ exp.title }}</div>
            <div class="cv-card-subtitle-row">
              <span class="cv-card-subtitle">{{ exp.companyName }}</span>
              <span class="cv-subtitle-meta">
                <wa-icon name="calendar" aria-hidden="true" />
                <span class="cv-date-range">{{ formatDateRange(exp.startDate, exp.endDate) }}</span>
                <span v-if="exp.location" class="cv-location">· {{ exp.location }}</span>
              </span>
            </div>
          </div>

          <div v-if="exp.description" class="cv-card-body cv-card-footer">
            {{ exp.description }}
          </div>
        </wa-card>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, inject, onMounted } from 'vue';
import type { AxiosInstance } from 'axios';
import type { ExperienceResponse } from '../types';
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
const experiences = ref<ExperienceResponse[]>([]);

const sortedExperiences = computed(() => {
  return [...experiences.value].sort((a, b) => {
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
    const response = await cvApi.get<ExperienceResponse[]>('/api/cv/experiences');
    experiences.value = response.data;
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to load experiences';
  } finally {
    loading.value = false;
  }
});
</script>
