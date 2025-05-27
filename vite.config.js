import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 5175,
    strictPort: true,
    host: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        ws: true,
        configure: (proxy, options) => {
          console.log('Vite proxy is configured for /api -> http://localhost:8080')
          
          proxy.on('error', (err, req, res) => {
            console.log('🔴 Proxy Error:', err.message)
            console.log('   Request URL:', req.url)
          })
          
          proxy.on('proxyReq', (proxyReq, req, res) => {
            proxyReq.setHeader('Origin', 'http://localhost:5175')
            proxyReq.setHeader('Host', 'localhost:8080')
            
            const color = '\x1b[36m%s\x1b[0m' // 青色
            console.log(color, '🔄 Proxying Request:')
            console.log(color, `   ${req.method} ${req.url}`)
            console.log(color, `   -> http://localhost:8080${req.url}`)
          })
          
          proxy.on('proxyRes', (proxyRes, req, res) => {
            const color = proxyRes.statusCode >= 400 ? '\x1b[31m%s\x1b[0m' : '\x1b[32m%s\x1b[0m'
            console.log(color, `📥 Proxy Response: ${proxyRes.statusCode}`)
            console.log(color, `   ${req.method} ${req.url}`)
            if (proxyRes.statusCode >= 400) {
              console.log(color, `   Response Headers: ${JSON.stringify(proxyRes.headers)}`)
              console.log(color, `   Request Headers: ${JSON.stringify(req.headers)}`)
            }
          })
        }
      }
    }
  }
})