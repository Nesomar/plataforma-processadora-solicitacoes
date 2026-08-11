/// <reference types="vitest/config" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // amazon-cognito-identity-js referencia `global` (Node) — não existe no browser
  define: {
    global: 'globalThis',
  },
  test: {
    environment: 'jsdom',
  },
})
