import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import mkcert from 'vite-plugin-mkcert'
import fs from 'fs';
import path from 'path';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), mkcert()],
  server: {
    port: 3000,
    https: {
      pfx: fs.readFileSync(path.resolve(__dirname, './springboot.p12')),
      passphrase: 'sigurnost'
    },
    proxy: {
      '/api': {
        secure: false
      }
    }
  }
})
