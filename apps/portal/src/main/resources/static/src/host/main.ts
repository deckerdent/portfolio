import { init, getInstance, loadRemote, registerRemotes } from '@module-federation/enhanced/runtime';
import { MicroFrontendApp } from './models/MicroFrontendApp';

/**
 * Bootstrap the host application
 */
async function bootstrap() {
    let host = init({
        name: 'host',
        remotes: [],
        shared: {},
    })

    let host2 = getInstance();

    console.log("another", host2)

    console.log(`Host instance created: ${host}`, host)
    // Module Federation already initialized as part of the Vite config, so we can directly load remotes
    let mf = registerRemotes([
        {
            name: 'portal',
            entry: '/mf-manifest.json',
        },
    ])

    console.log(`Registered remotes: ${mf}`)
    console.log(host)

    // Load all three remote modules
    const defaultApp = await loadRemote<{ mount: (container: HTMLElement) => void }>("portal/DefaultApp");
    const testApp = await loadRemote<{ mount: (container: HTMLElement) => void }>("portal/TestApp");
    const HelloComponent = await loadRemote<any>("portal/HelloComponent");

    //console.log(`Remote modules loaded:`, { defaultApp, testApp, HelloComponent })

    // Mount Default App to sidebar-left
    const sidebarLeft = document.getElementById('slot:sidebar-left');
    if (sidebarLeft && defaultApp?.mount) {
        defaultApp.mount(sidebarLeft);
        console.log('Mounted DefaultApp to sidebar-left');
    } else {
        console.error('Could not mount DefaultApp: container or mount function not found');
    }

    // Mount Test App to sidebar-right
    const sidebarRight = document.getElementById('slot:sidebar-right');
    if (sidebarRight && testApp?.mount) {
        testApp.mount(sidebarRight);
        console.log('Mounted TestApp to sidebar-right');
    } else {
        console.error('Could not mount TestApp: container or mount function not found');
    }

    // Register and mount HelloComponent custom element to header
    const header = document.getElementById('slot:header');
    if (header && HelloComponent?.default) {
        // Register the custom element
        if (!customElements.get('hello-component')) {
            customElements.define('hello-component', HelloComponent.default);
        }
        // Create and append the custom element
        const helloElement = document.createElement('hello-component');
        header.appendChild(helloElement);
        console.log('Mounted HelloComponent to header');
    } else {
        console.error('Could not mount HelloComponent: container or component not found');
    }
}

// Start the application using top-level await
await bootstrap();
