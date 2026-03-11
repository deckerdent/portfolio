<template>
  <div class="skills-view">
    <wa-spinner v-if="loading" />
    <wa-callout v-else-if="error" variant="danger" open>
      <wa-icon slot="icon" name="exclamation-triangle" />
      {{ error }}
    </wa-callout>
    <div v-else class="cv-skills-layout">
      <div class="cv-skills-section">
        <h2 class="cv-section-heading">Skills</h2>
        <div class="cv-skills-list">
          <div v-for="skill in skills" :key="skill.id" class="cv-skill-item">
            <span class="cv-skill-name">{{ skill.title }}</span>
            <wa-progress-bar :value="skill.level * 10" />
          </div>
        </div>
      </div>

      <div class="cv-competences-section">
        <h2 class="cv-section-heading">Competences</h2>
        <div class="cv-competences-list">
          <wa-badge
            v-for="comp in competences"
            :key="comp.id"
            appearance="warning"
          >
            {{ comp.description }}
          </wa-badge>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, inject, onMounted } from 'vue';
import type { AxiosInstance } from 'axios';
import type { SkillResponse, CompetenceResponse } from '../types';
import '@awesome.me/webawesome/dist/styles/webawesome.css';
import '@awesome.me/webawesome/dist/components/spinner/spinner.js';
import '@awesome.me/webawesome/dist/components/callout/callout.js';
import '@awesome.me/webawesome/dist/components/icon/icon.js';
import '@awesome.me/webawesome/dist/components/progress-bar/progress-bar.js';
import '@awesome.me/webawesome/dist/components/badge/badge.js';
import { allDefined } from '@awesome.me/webawesome/dist/utilities/defined.js';

await allDefined();

const cvApi = inject<AxiosInstance>('cvApi')!;

const loading = ref(true);
const error = ref<string | null>(null);
const skills = ref<SkillResponse[]>([]);
const competences = ref<CompetenceResponse[]>([]);

onMounted(async () => {
  try {
    const [skillsRes, competencesRes] = await Promise.all([
      cvApi.get<SkillResponse[]>('/api/cv/skills'),
      cvApi.get<CompetenceResponse[]>('/api/cv/competences'),
    ]);
    skills.value = skillsRes.data;
    competences.value = competencesRes.data;
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to load skills and competences';
  } finally {
    loading.value = false;
  }
});
</script>
