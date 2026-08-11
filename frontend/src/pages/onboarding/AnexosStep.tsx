import { useState } from "react";
import type { FormEvent } from "react";
import { anexosApi } from "../../api/anexosApi";

// Etapa final do wizard (design.md): não é gated pelo backend como as outras (perfil já
// está completo aqui) — o cliente pode seguir pro dashboard mesmo sem enviar anexo agora.
export function AnexosStep({ onContinuar }: { onContinuar: () => void }) {
  const [arquivo, setArquivo] = useState<File | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);
  const [enviado, setEnviado] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!arquivo) return;
    setError(null);
    setEnviando(true);
    try {
      await anexosApi.enviar(arquivo);
      setEnviado(true);
    } catch {
      setError("Não foi possível enviar o arquivo. Tente novamente.");
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div>
      <h2>Anexos</h2>
      <form onSubmit={handleSubmit}>
        <label>
          Documento (PDF)
          <input
            type="file"
            accept="application/pdf"
            onChange={(e) => setArquivo(e.target.files?.[0] ?? null)}
          />
        </label>
        {error && <p role="alert">{error}</p>}
        {enviado && <p>Arquivo enviado.</p>}
        <button type="submit" disabled={!arquivo || enviando}>
          {enviando ? "Enviando..." : "Enviar"}
        </button>
      </form>
      <button type="button" onClick={onContinuar}>
        Continuar
      </button>
    </div>
  );
}
