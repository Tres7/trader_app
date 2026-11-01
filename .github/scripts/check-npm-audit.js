#!/usr/bin/env node
const { spawnSync } = require('child_process');

const EXCLUDED = new Set([
  'GHSA-mh99-v99m-4gvg',
  'GHSA-rgw5-rvv9-x895',
  'GHSA-w3rx-r6r6-pgpr',
  'GHSA-5p2g-fcmc-qvqq',
  // braces: no fixed version published upstream yet (3.0.3 is latest on npm)
  'GHSA-vfj7-8cjw-p6xm',
  // decode-uri-component: fixed only in 0.5.1, which is ESM-only, while query-string@7
  // (via expo-router) loads it with require() and pins ^0.2.2 - an override would break it
  'GHSA-vcc3-ghjq-m6fr',
  'GHSA-qr28-fpjm-wq7f',
]);

const result = spawnSync('npm audit --json', { encoding: 'utf8', maxBuffer: 1024 * 1024 * 50, shell: true });
const data = JSON.parse(result.stdout);

const failures = new Set();
for (const vuln of Object.values(data.vulnerabilities || {})) {
  if (vuln.severity !== 'high' && vuln.severity !== 'critical') continue;
  for (const via of vuln.via) {
    if (typeof via !== 'object' || !via.url) continue;
    const id = via.url.split('/').pop();
    if (!EXCLUDED.has(id)) {
      failures.add(`${vuln.severity.toUpperCase()} ${vuln.name}: ${via.title} (${id})`);
    }
  }
}

if (failures.size > 0) {
  console.error('npm audit found unexcused high/critical vulnerabilities:\n');
  console.error([...failures].join('\n'));
  process.exit(1);
}

console.log(`npm audit clean (excluding: ${[...EXCLUDED].join(', ')})`);
process.exit(0);
