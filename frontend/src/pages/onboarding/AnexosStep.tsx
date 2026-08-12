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
    <div className="form">
      <h2>Anexos</h2>
      <form onSubmit={handleSubmit} className="form">
        <div className="field">
          <label className="field__label" htmlFor="anexo-arquivo">
            Documento (PDF)
          </label>
          <input
            id="anexo-arquivo"
            className="input"
            type="file"
            accept="application/pdf"
            onChange={(e) => setArquivo(e.target.files?.[0] ?? null)}
          />
        </div>
        {error && (
          <p className="alert" role="alert">
            {error}
          </p>
        )}
        {enviado && (
          <p className="confirm" role="status">
            Arquivo enviado.
          </p>
        )}
        <button type="submit" className="button button--secondary" disabled={!arquivo || enviando}>
          {enviando ? "Enviando..." : "Enviar"}
        </button>
      </form>
      <button type="button" className="button button--primary" onClick={onContinuar}>
        Continuar
      </button>
    </div>
  );
}
