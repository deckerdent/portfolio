<template>
    <div class="setup-view">
        <div class="setup-welcome">
            <p class="setup-welcome-heading">Welcome</p>
            <p class="setup-welcome-text">
                We're pleased you found your way to this project. It's a public demo project
                aiming to demonstrate an idea of how a micro-frontend based multi-app web portal
                could be set up. Please fill the form below to set up this portal initially.
            </p>
        </div>

        <wa-card>
            <span slot="header">Portal Setup</span>

            <form class="setup-form" @submit="onSubmit">
                <wa-input
                    label="Title"
                    name="title"
                    required
                    autocomplete="off"
                    :value="title"
                    @wa-input="title = ($event.target as HTMLInputElement).value"
                />

                <wa-input
                    label="Base Path"
                    name="basePath"
                    required
                    :value="basePath"
                    @wa-input="basePath = ($event.target as HTMLInputElement).value"
                />

                <wa-alert v-if="error" variant="danger" open>
                    <wa-icon slot="icon" name="exclamation-triangle" />
                    {{ error }}
                </wa-alert>

                <wa-button
                    type="submit"
                    variant="brand"
                    :loading="submitting || undefined"
                >
                    Set Up Portal
                </wa-button>
            </form>
        </wa-card>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { axiosInstance, PortalStore } from '@portfolio/core';
import '../styles.css';
import '@awesome.me/webawesome/dist/styles/webawesome.css';
import '@awesome.me/webawesome/dist/components/button/button.js';
import '@awesome.me/webawesome/dist/components/input/input.js';
import '@awesome.me/webawesome/dist/components/card/card.js';
import { allDefined } from '@awesome.me/webawesome/dist/utilities/defined.js';

await allDefined();

const host = PortalStore.getInstance().host;
const title = ref(host?.title ?? '');
const basePath = ref(host?.basePath ?? '/');

const submitting = ref(false);
const error = ref<string | null>(null);

const onSubmit = async (event: Event) => {
    event.preventDefault();
    if (!title.value.trim()) return;

    submitting.value = true;
    error.value = null;

    try {
        await axiosInstance.post('/api/host/config', { key: 'title', value: title.value.trim() });
        await axiosInstance.post('/api/host/config', { key: 'basePath', value: basePath.value || '/' });
        await axiosInstance.post('/api/host/config', { key: 'appsUrl', value: 'apps.json' });
        await axiosInstance.post('/api/host/config', { key: 'sourceUrls', value: 'sources.json' });
        window.location.reload();
    } catch (err: unknown) {
        submitting.value = false;
        error.value = err instanceof Error ? err.message : 'An unexpected error occurred. Please try again.';
    }
};
</script>
