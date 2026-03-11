import { defineCustomElement, h, ref, onMounted, onUnmounted } from 'vue';
import { loadRemote } from '@module-federation/enhanced/runtime';
import { PortalStore } from '@portfolio/core';
import type { App } from '@portfolio/core';
import type { ModuleLifecycle } from '@portfolio/core';
import type { Subscription } from 'rxjs';

await loadRemote('portal/PortalLink');
//TODO: bit much of configurations, need to review those
const PortalHeaderDefinition = defineCustomElement({
    props: {
        logoUrl: { type: String, default: '/wordmark.png' },
    },
    setup(props) {
        const apps = ref<App[]>([]);
        let subscription: Subscription | null = null;

        onMounted(() => {
            subscription = PortalStore.apps$.subscribe(all => {
                apps.value = all.filter(app => app.slot === 'app');
            });
        });

        onUnmounted(() => {
            subscription?.unsubscribe();
        });

        return () =>
            h('div', { part: 'root' }, [
                h('header', { part: 'header' }, [
                    h('div', { part: 'logo' }, [
                        h('slot', { name: 'logo' }),
                        h('img', {
                            part: 'logo-img',
                            src: props.logoUrl,
                            alt: 'Logo',
                            'aria-hidden': 'true',
                        }),
                    ]),
                ]),
                h('nav', { part: 'nav' }, [
                    ...apps.value.map(app =>
                        h('portal-link', {
                            href: app.activationUrl,
                            part: 'nav-link',
                        }, app.displayName),
                    ),
                ]),
            ]);
    },

    styles: [`
        :host {
            display: block;
            font-family: inherit;
            position: sticky;
            top: 0;
            z-index: 20;

            --portal-header-bg: linear-gradient(135deg, #194430 0%, #528C5C 100%);
            --portal-header-border-color: #163527;
            --portal-header-height: var(--wa-space-3xl);
            --portal-header-logo-size: var(--wa-space-3xl);
            --portal-header-logo-radius: var(--wa-border-radius-m);
            --portal-header-nav-bg: var(--wa-color-surface-raised);
            --portal-header-nav-gap: var(--wa-space-xs);
            --portal-header-nav-padding-block: var(--wa-space-xs);
            --portal-header-nav-padding-inline: var(--wa-space-l);
        }

        /* Root wrapper */
        [part="root"] {
            display: flex;
            flex-direction: column;
            border: var(--wa-border-width-s) var(--wa-border-style) var(--portal-header-border-color);
            border-radius: var(--wa-border-radius-s);
            margin: var(--wa-space-s);
            overflow: hidden;
            background: var(--wa-color-surface-raised);
        }

        /* Header bar */
        [part="header"] {
            display: flex;
            align-items: center;
            height: var(--portal-header-height);
            padding-inline: var(--portal-header-nav-padding-inline);
            background: var(--portal-header-bg);
            border-bottom: var(--wa-border-width-s) var(--wa-border-style) var(--portal-header-border-color);
        }

        /* Logo container */
        [part="logo"] {
            display: contents;
        }

        /* Slotted logo content */
        ::slotted([slot="logo"]) {
            display: block;
            height: var(--portal-header-logo-size);
            width: auto;
        }

        /* Default logo image */
        [part="logo-img"] {
            display: block;
            height: var(--portal-header-logo-size);
            width: auto;
            object-fit: contain;
        }

        /* Hide the default img when a named slot has projected content */
        :host(:has([slot="logo"])) [part="logo-img"] {
            display: none;
        }

        /* Navigation bar */
        [part="nav"] {
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            gap: var(--portal-header-nav-gap);
            padding-block: var(--portal-header-nav-padding-block);
            padding-inline: var(--portal-header-nav-padding-inline);
            background: var(--portal-header-nav-bg);
            border-bottom: var(--wa-border-width-s) var(--wa-border-style) var(--portal-header-border-color);
        }

        /* Nav links — styles the portal-link host element */
        ::slotted(*),
        portal-link[part="nav-link"] {
            display: inline-flex;
            align-items: center;
            padding-block: var(--wa-space-2xs);
            padding-inline: var(--wa-space-s);
            border-radius: var(--wa-border-radius-square);
            border-bottom: 0.1875rem solid transparent;
            font-size: var(--wa-font-size-s);
            font-weight: var(--wa-font-weight-action);
            color: var(--wa-color-brand-border-loud);
            text-decoration: none;
            transition:
                background var(--wa-transition-normal) var(--wa-transition-easing),
                color var(--wa-transition-normal) var(--wa-transition-easing);
            --portal-link-color: var(--wa-color-brand-border-loud);
            --portal-link-hover-color: var(--wa-color-brand-border-normal);
        }

        portal-link[part="nav-link"]:hover {
            background: color-mix(in oklab, var(--wa-color-brand-fill-quiet) 72%, transparent);
            border-bottom-color: var(--wa-color-brand-border-normal);
        }

        portal-link[part="nav-link"]:focus-within {
            outline: var(--wa-focus-ring);
            outline-offset: var(--wa-focus-ring-offset);
        }
    `],
});

class PortalHeader extends PortalHeaderDefinition {
    override connectedCallback(): void {
        super.connectedCallback();
    }
}

customElements.define('portal-header', PortalHeader);

let _element: HTMLElement | null = null;

export const mount: ModuleLifecycle['mount'] = (container: HTMLElement) => {
    _element ??= document.createElement('portal-header');
    container.appendChild(_element);
};

export const unmount: NonNullable<ModuleLifecycle['unmount']> = () => {
    _element?.remove();
};

export { PortalHeader };
