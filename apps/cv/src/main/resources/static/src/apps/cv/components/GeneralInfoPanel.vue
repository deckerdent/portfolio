<template>
  <div class="general-info-panel">
    <wa-spinner v-if="loading" />
    <div v-else-if="generalInfo" class="cv-info-layout">
      <div class="cv-avatar">
        <img
          v-if="generalInfo.imageUrl"
          :src="cvBaseUrl + generalInfo.imageUrl"
          :alt="`${generalInfo.firstName} ${generalInfo.lastName}`"
        />
        <div v-else class="cv-avatar-initials">
          {{ getInitials(generalInfo.firstName, generalInfo.lastName) }}
        </div>
      </div>

      <div class="cv-info-column">
        <h1 class="cv-full-name">{{ generalInfo.firstName }} {{ generalInfo.lastName }}</h1>
        
        <div class="cv-info-line">
          <span v-if="generalInfo.nationality">{{ generalInfo.nationality }}</span>
          <span v-if="generalInfo.dateOfBirth">
            {{ generalInfo.nationality ? ' · ' : '' }}{{ formatFullDate(generalInfo.dateOfBirth) }}
          </span>
        </div>

        <div class="cv-info-line">
          <span v-if="generalInfo.maritalStatus">{{ displayMaritalStatus(generalInfo.maritalStatus) }}</span>
          <span v-if="generalInfo.numberOfChildren && generalInfo.numberOfChildren > 0">
            {{ generalInfo.maritalStatus ? ' · ' : '' }}{{ generalInfo.numberOfChildren }} {{ generalInfo.numberOfChildren === 1 ? 'child' : 'children' }}
          </span>
        </div>

        <p v-if="generalInfo.summary" class="cv-summary">{{ generalInfo.summary }}</p>

        <div v-if="languages.length > 0" class="cv-languages-row">
          <span class="cv-label">Languages</span>
          <div class="cv-languages-list">
            <div v-for="lang in languages" :key="lang.id" class="cv-language-item">
              <span class="cv-language-name">{{ lang.name }}</span>
              <span class="cv-language-level">
                <wa-icon v-for="i in lang.level" :key="`filled-${i}`" name="star" />
                <wa-icon v-for="i in (10 - lang.level)" :key="`empty-${i}`" name="star" library="far" />
              </span>
            </div>
          </div>
        </div>

        <div v-if="hobbies.length > 0" class="cv-hobbies-row">
          <span class="cv-label">Hobbies:</span>
          <div class="cv-hobbies-list">
            <wa-badge v-for="hobby in hobbies" :key="hobby.id" size="small" pill>
              <span>{{ hobby.name }}</span>
            </wa-badge>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, inject, onMounted } from 'vue';
import type { AxiosInstance } from 'axios';
import type { GeneralInfoResponse, LanguageResponse, HobbyResponse } from '../types';
import '@awesome.me/webawesome/dist/styles/webawesome.css';
import '@awesome.me/webawesome/dist/components/spinner/spinner.js';
import '@awesome.me/webawesome/dist/components/badge/badge.js';
import '@awesome.me/webawesome/dist/components/icon/icon.js';
import { allDefined } from '@awesome.me/webawesome/dist/utilities/defined.js';

await allDefined();

const cvApi = inject<AxiosInstance>('cvApi')!;
const cvBaseUrl = cvApi.defaults.baseURL ?? '';

const loading = ref(true);
const generalInfo = ref<GeneralInfoResponse | null>(null);
const languages = ref<LanguageResponse[]>([]);
const hobbies = ref<HobbyResponse[]>([]);

const MARITAL_STATUS_LABELS: Record<string, string> = {
  SINGLE: 'Single',
  MARRIED: 'Married',
  DIVORCED: 'Divorced',
  WIDOWED: 'Widowed',
  SEPARATED: 'Separated',
};

const displayMaritalStatus = (status: string | null) =>
  status ? (MARITAL_STATUS_LABELS[status] ?? status) : null;

const getInitials = (firstName: string, lastName: string) =>
  `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();

const formatFullDate = (iso: string) =>
  new Intl.DateTimeFormat('en', { year: 'numeric', month: 'long', day: 'numeric' }).format(new Date(iso));

onMounted(async () => {
  try {
    const [infoRes, langsRes, hobbiesRes] = await Promise.all([
      cvApi.get<GeneralInfoResponse>('/api/cv/general-info'),
      cvApi.get<LanguageResponse[]>('/api/cv/languages'),
      cvApi.get<HobbyResponse[]>('/api/cv/hobbies'),
    ]);
    generalInfo.value = infoRes.data;
    languages.value = langsRes.data;
    hobbies.value = hobbiesRes.data;
  } catch (e) {
    console.error('Failed to load general info:', e);
  } finally {
    loading.value = false;
  }
});
</script>
