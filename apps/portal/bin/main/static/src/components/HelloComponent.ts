import { defineCustomElement, h } from 'vue';

const HelloComponent = defineCustomElement({
    render() {
        return h('div', { class: 'hello-component' }, 'hello from component');
    },
    styles: [`
    .hello-component {
      padding: 1rem;
      color: #fff;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 8px;
      font-family: system-ui, -apple-system, sans-serif;
      font-size: 1.125rem;
      font-weight: 500;
      text-align: center;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
  `]
});

export default HelloComponent;
