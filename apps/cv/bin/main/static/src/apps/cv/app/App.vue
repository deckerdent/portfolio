<template>
  <div class="cv-layout">
    <aside class="cv-sidebar">
      <Suspense>
        <GeneralInfoPanel />
      </Suspense>
    </aside>

    <main class="cv-main">
      <nav class="cv-tabs">
        <RouterLink to="/timeline">Timeline</RouterLink>
        <RouterLink to="/experience">Experience</RouterLink>
        <RouterLink to="/education">Education</RouterLink>
        <RouterLink to="/skills">Skills & Competences</RouterLink>
        <RouterLink to="/certificates-references">Certificates & References</RouterLink>
      </nav>

      <div class="cv-content">
        <Suspense>
          <RouterView />
        </Suspense>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import GeneralInfoPanel from '../components/GeneralInfoPanel.vue';
</script>
