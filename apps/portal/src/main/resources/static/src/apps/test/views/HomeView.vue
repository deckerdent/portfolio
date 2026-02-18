<script setup lang="ts">
// Test App - Different from Default
</script>

<template>
  <main class="test-home">
    <div class="hero">
      <h1>🧪 Test Application</h1>
      <p>This is a separate Vue application for testing purposes</p>
      <div class="features">
        <div class="feature-card">
          <h3>Module Federation</h3>
          <p>Independently loaded remote module</p>
        </div>
        <div class="feature-card">
          <h3>Isolated Router</h3>
          <p>Own routing instance and state</p>
        </div>
        <div class="feature-card">
          <h3>Shared Dependencies</h3>
          <p>Vue and Vue Router shared via MF</p>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.test-home {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.hero {
  text-align: center;
}

.hero h1 {
  font-size: 3rem;
  color: #42b983;
  margin-bottom: 1rem;
}

.hero p {
  font-size: 1.25rem;
  color: #666;
  margin-bottom: 2rem;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-top: 3rem;
}

.feature-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-card h3 {
  margin-top: 0;
  font-size: 1.5rem;
}

.feature-card p {
  margin-bottom: 0;
  opacity: 0.9;
}
</style>
