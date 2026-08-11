// Em memória (não localStorage): um XSS não consegue ler o token do storage.
// Trade-off: reload de página perde a sessão (sem refresh-token silencioso nesta fase).
let idToken: string | null = null;

export const tokenStore = {
  get: (): string | null => idToken,
  set: (token: string): void => {
    idToken = token;
  },
  clear: (): void => {
    idToken = null;
  },
};
