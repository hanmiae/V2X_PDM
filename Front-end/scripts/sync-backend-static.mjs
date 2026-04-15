/**
 * Copies Vite `dist/` into Back-end Spring static resources (overwrites).
 * Run from Front-end: npm run deploy:static
 */
import { cp, rm } from 'node:fs/promises'
import { join, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = dirname(fileURLToPath(import.meta.url))
const frontRoot = join(__dirname, '..')
const distDir = join(frontRoot, 'dist')
const staticDir = join(frontRoot, '..', 'Back-end', 'src', 'main', 'resources', 'static')
const assetsDir = join(staticDir, 'assets')

await rm(assetsDir, { recursive: true, force: true })
await cp(distDir, staticDir, { recursive: true })
console.log(`Synced ${distDir} -> ${staticDir}`)
