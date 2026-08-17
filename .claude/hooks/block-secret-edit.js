let data = '';
process.stdin.on('data', (c) => (data += c));
process.stdin.on('end', () => {
  let input;
  try {
    input = JSON.parse(data);
  } catch {
    return;
  }
  const file = (input.tool_input && input.tool_input.file_path) || '';
  const normalized = file.replace(/\\/g, '/');
  const blocked = /(^|\/)\.env(\..*)?$/.test(normalized) || /\.tfvars$/.test(normalized);
  if (blocked) {
    console.log(
      JSON.stringify({
        hookSpecificOutput: {
          hookEventName: 'PreToolUse',
          permissionDecision: 'deny',
          permissionDecisionReason:
            'Edicao bloqueada: arquivo de segredo (.env/.tfvars). Edite manualmente fora do Claude Code se necessario.',
        },
      })
    );
  }
});
