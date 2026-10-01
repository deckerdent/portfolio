import { defineCustomElement, h } from 'vue';

const PORTAL_LINK_CONNECTED = 'portal-link:connected';
const PORTAL_NAVIGATE = 'portal-navigate';

const PortalLinkDefinition = defineCustomElement({
    props: {
        href: { type: String, default: '' },
    },

    setup(props) {
        if (import.meta.env.DEV && !props.href) {
            console.warn('<portal-link>: missing required `href` attribute. Defaulting to "/".');
        }

        return () =>
            h(
                'a',
                { href: props.href || '/', part: 'anchor' },
                h('slot'),
            );
    },

    styles: [`
        :host {
            display: inline;
        }
        a {
            color: var(--portal-link-color, inherit);
            text-decoration: var(--portal-link-decoration, inherit);
        }
        :host(:hover) a,
        :host(:focus-within) a {
            color: var(--portal-link-hover-color, var(--portal-link-color, inherit));
        }
    `],
});

class PortalLink extends PortalLinkDefinition {
    private readonly _handleClick = (e: MouseEvent): void => {
        e.preventDefault();
        this.dispatchEvent(
            new CustomEvent(PORTAL_NAVIGATE, {
                detail: { href: this.getAttribute('href') || '/' },
                bubbles: true,
                composed: true,
            }),
        );
    };

    override connectedCallback(): void {
        super.connectedCallback();
        this.addEventListener('click', this._handleClick);
        this.dispatchEvent(
            new CustomEvent(PORTAL_LINK_CONNECTED, { bubbles: true, composed: true }),
        );
    }

    override disconnectedCallback(): void {
        super.disconnectedCallback();
        this.removeEventListener('click', this._handleClick);
    }
}

customElements.define('portal-link', PortalLink);

export { PortalLink };
