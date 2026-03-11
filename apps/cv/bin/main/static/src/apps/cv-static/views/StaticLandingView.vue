<template>
  <main class="cv-static-page">
    <section class="cv-static-banner" aria-label="Banner placeholder">
      <span>Banner Placeholder</span>
    </section>

    <section class="cv-static-welcome">
      <h1>Welcome</h1>
      <p>
        Welcome to this static CV module page. This view demonstrates a simple
        single-view layout with alternating image and text sections.
      </p>
    </section>

    <section class="cv-static-row">
      <figure class="cv-static-image">
        <img :src="leftImagePath" alt="Placeholder visual on the left" />
      </figure>
      <article class="cv-static-text">
        <h2>Left Image / Right Text</h2>
        <p>
          This section keeps the image placeholder on the left and descriptive
          content on the right. The image source path is statically defined.
        </p>
      </article>
    </section>

    <section class="cv-static-row cv-static-row--reverse-mobile">
      <article class="cv-static-text">
        <h2>Left Text / Right Image</h2>
        <p>
          The second section mirrors the layout with text first and image second,
          suitable for simple landing-page storytelling.
        </p>
      </article>
      <figure class="cv-static-image">
        <img :src="rightImagePath" alt="Placeholder visual on the right" />
      </figure>
    </section>
  </main>
</template>

<script setup lang="ts">
const leftImagePath = '/images/cv-static/placeholder-left.svg';
const rightImagePath = '/images/cv-static/placeholder-right.svg';
</script>
