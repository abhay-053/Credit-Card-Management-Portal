/// <reference types="vite/client" />
// tell TypeScript how to import .vue files
declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}