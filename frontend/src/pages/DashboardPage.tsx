import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { isAxiosError } from "axios";
import type { Solicitacao } from "../api/solicitacoesApi";
import { solicitacoesApi } from "../api/solicitacoesApi";

export function DashboardPage() {
  const [solicitacoes, setSolicitacoes] = useState<Solicitacao[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [processando, setProcessando] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    void carregar();
  }, []);

  async function carregar() {
    try {
      setSolicitacoes(await solicitacoesApi.listar());
    } catch {
      setError("Não foi possível carregar suas solicitações.");
    }
  }

  const abertaExistente = solicitacoes?.find((s) => s.status === "ABERTA") ?? null;

  // Uma ABERTA por vez (specs/service-requests/spec.md): se já existe, continua editando
  // ela em vez de criar outra — o backend também é idempotente aqui, mas evitar a chamada
  // poupa uma ida ao servidor quando já sabemos a resposta.
  async function novaSolicitacao() {
    setError(null);
    setProcessando(true);
    try {
      if (abertaExistente) {
        navigate(`/solicitacoes/${abertaExistente.id}`);
        return;
      }
      const criada = await solicitacoesApi.criar();
      navigate(`/solicitacoes/${criada.id}`);
    } catch (err) {
      // Perfil incompleto: backend recusa com 409 (specs/service-requests/spec.md)
      if (isAxiosError(err) && err.response?.status === 409) {
        navigate("/onboarding");
        return;
      }
      setError("Não foi possível criar a solicitação.");
    } finally {
      setProcessando(false);
    }
  }

  return (
    <>
      <header className="topbar">
        <span className="topbar__brand">Portal do Cliente</span>
      </header>
      <div className="shell shell--painel">
        <div className="painel__header">
          <h1>Minhas solicitações</h1>
          <div className="painel__acoes">
            <Link to="/anexos" className="button button--secondary">
              Meus anexos
            </Link>
            <button type="button" className="button button--primary" onClick={novaSolicitacao} disabled={processando}>
              {processando ? "Aguarde..." : abertaExistente ? "Continuar solicitação" : "Nova solicitação"}
            </button>
          </div>
        </div>
        {error && (
          <p className="alert" role="alert">
            {error}
          </p>
        )}
        {solicitacoes && solicitacoes.length === 0 && (
          <p className="empty-state">Nenhuma solicitação ainda. Crie a primeira acima.</p>
        )}
        {solicitacoes && solicitacoes.length > 0 && (
          <ul className="lista-solicitacoes">
            {solicitacoes.map((s) => (
              <li key={s.id} className="lista-solicitacoes__item">
                <Link
                  to={`/solicitacoes/${s.id}`}
                  className="lista-solicitacoes__link"
                  data-status={s.status}
                >
                  <span className="protocolo">{s.id}</span>
                  <span className="status-badge" data-status={s.status}>
                    {s.status}
                  </span>
                </Link>
              </li>
            ))}
          </ul>
        )}
      </div>
    </>
  );
}
