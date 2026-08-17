import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import type { Anexo } from "../api/anexosApi";
import { anexosApi } from "../api/anexosApi";

// Visualização inline, sem link/botão de download (specs/attachments/spec.md) — o "salvar
// como" nativo do visualizador de PDF do navegador continua fora do controle da aplicação.
export function AnexosPage() {
  const [anexos, setAnexos] = useState<Anexo[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [selecionado, setSelecionado] = useState<Anexo | null>(null);
  const [objectUrl, setObjectUrl] = useState<string | null>(null);

  useEffect(() => {
    anexosApi
      .listar()
      .then(setAnexos)
      .catch(() => setError("Não foi possível carregar seus anexos."));
  }, []);

  useEffect(() => {
    return () => {
      if (objectUrl) URL.revokeObjectURL(objectUrl);
    };
  }, [objectUrl]);

  async function visualizar(anexo: Anexo) {
    setError(null);
    setSelecionado(anexo);
    if (objectUrl) URL.revokeObjectURL(objectUrl);
    setObjectUrl(null);
    try {
      const blob = await anexosApi.visualizar(anexo.id);
      setObjectUrl(URL.createObjectURL(blob));
    } catch {
      setError("Não foi possível abrir este anexo.");
      setSelecionado(null);
    }
  }

  return (
    <>
      <header className="topbar">
        <span className="topbar__brand">Portal do Cliente</span>
      </header>
      <div className="shell shell--painel">
        <div className="painel__header">
          <h1>Meus anexos</h1>
          <Link to="/" className="button button--secondary">
            ← Voltar
          </Link>
        </div>
        {error && (
          <p className="alert" role="alert">
            {error}
          </p>
        )}
        {anexos && anexos.length === 0 && <p className="empty-state">Nenhum anexo enviado ainda.</p>}
        {anexos && anexos.length > 0 && (
          <ul className="lista-solicitacoes">
            {anexos.map((a) => (
              <li key={a.id} className="lista-solicitacoes__item">
                <button
                  type="button"
                  className="lista-anexos__link"
                  onClick={() => visualizar(a)}
                >
                  {a.nomeArquivo}
                </button>
              </li>
            ))}
          </ul>
        )}
        {selecionado && objectUrl && (
          <div className="card">
            <h2>{selecionado.nomeArquivo}</h2>
            <embed src={objectUrl} type="application/pdf" width="100%" height="600" />
          </div>
        )}
      </div>
    </>
  );
}
