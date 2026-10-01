<template>
  <main class="cv-static-page">
    <section class="cv-static-banner" aria-label="Banner placeholder">
      <span>Banner Placeholder</span>
    </section>

    <section class="cv-static-welcome">
      <h1>Welcome</h1>
      <p>
        This is a demo project. It's primary purpose is to showcase a self-contained system based architecture incorporating flexible, runtime integrated federated modules in a single web presence.<br/><br/>
        Advanced features such as auth, user management with SCIM, or even more advanced apps or dynamically registering them may or may not be implemented.<br/><br/>


        For further information, known issues or planned features of this project, please read the readme of the project.<br/><br/>
        For more information about me read the sections below or switch to the CV app in the navigation. 
      </p>
    </section>

    <section class="cv-static-row">
      <figure class="cv-static-image">
        <wa-comparison>
          <img
            slot="before"
            :src="beforeImageSectionOne"
            alt="Heidelberg Altstadt black and white."
          />
          <img
            slot="after"
            :src="afterImageSectionOne"
            alt="Berlin Ebaswalder Straße black and white. Several people walking on the sidewalk."
          />
        </wa-comparison>
      </figure>
      <article class="cv-static-text">
        <h2>Two Homes</h2>
        <p>
          Heidelberg is where my roots are—where I learned, grew, and built my foundation. Berlin, on the other hand, captured my spirit. I lived there for ten vibrant years, surrounded by creativity, pace, and possibility. Both cities shaped me in their own way: Heidelberg with its calm precision and Berlin with its restless energy. Returning to Heidelberg now feels less like leaving Berlin behind and more like bringing a piece of it home.
        </p>
      </article>
    </section>

    <section class="cv-static-row cv-static-row--reverse-mobile">
      <article class="cv-static-text">
        <h2>Two Professions</h2>
        <p>
          My career began with vocational training in logistics, specializing in heavy industries. Working in warehouses gave me practical insight into industrial operations and supply chain processes. Alongside my job, I completed the Bachelor Professional of Business (IHK), strengthening my understanding of business management. Later, I decided to combine this experience with technology by earning a Bachelor of Science in Business Informatics, building a bridge between business processes and IT solutions.
        </p>
      </article>
      <figure class="cv-static-image">
        <wa-comparison>
          <img
            slot="before"
            :src="beforeImageSectionTwo"
            alt="Grayscale version of kittens in a basket looking around."
          />
          <img
            slot="after"
            :src="afterImageSectionTwo"
            alt="Color version of kittens in a basket looking around."
          />
        </wa-comparison>
      </figure>
    </section>
  </main>
</template>

<script setup lang="ts">
const origin = new URL(import.meta.url).origin
import '@awesome.me/webawesome/dist/components/comparison/comparison.js';
const afterImageSectionOne = `${origin}/images/cv-static/UE.jpg`;
const beforeImageSectionOne = `${origin}/images/cv-static/HD.jpg`;

const afterImageSectionTwo = `${origin}/images/cv-static/OFFICE.jpg`;
const beforeImageSectionTwo = `${origin}/images/cv-static/FORK.jpg`;
</script>
