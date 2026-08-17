const path = require('path');
const { execFileSync } = require('child_process');

let data = '';
process.stdin.on('data', (c) => (data += c));
process.stdin.on('end', () => {
  let input;
  try {
    input = JSON.parse(data);
  } catch {
    return;
  }
  const file = input.tool_input && input.tool_input.file_path;
  if (!file) return;
  const normalized = file.replace(/\\/g, '/');
  if (!/\/frontend\/src\/.*\.tsx?$/.test(normalized)) return;
  const frontendDir = path.join(__dirname, '..', '..', 'frontend');
  const oxlintBin = path.join(frontendDir, 'node_modules', 'oxlint', 'bin', 'oxlint');
  try {
    execFileSync(process.execPath, [oxlintBin, file], {
      cwd: frontendDir,
      stdio: 'inherit',
    });
  } catch {
    // oxlint sai != 0 quando acha problema; hook so reporta, nao derruba a edicao
  }
});
