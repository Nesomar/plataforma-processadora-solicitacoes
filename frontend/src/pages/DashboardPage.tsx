import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { isAxiosError } from "axios";
import type { Solicitacao } from "../api/solicitacoesApi";
import { solicitacoesApi } from "../api/solicitacoesApi";

export function DashboardPage() {
  const [solicitacoes, setSolicitacoes] = useState<Solicitacao[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [criando, setCriando] = useState(false);
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

  async function novaSolicitacao() {
    setError(null);
    setCriando(true);
    try {
      await solicitacoesApi.criar();
      await carregar();
    } catch (err) {
      // Perfil incompleto: backend recusa com 409 (specs/service-requests/spec.md)
      if (isAxiosError(err) && err.response?.status === 409) {
        navigate("/onboarding");
        return;
      }
      setError("Não foi possível criar a solicitação.");
    } finally {
      setCriando(false);
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
          <button type="button" className="button button--primary" onClick={novaSolicitacao} disabled={criando}>
            {criando ? "Criando..." : "Nova solicitação"}
          </button>
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
                <Link to={`/solicitacoes/${s.id}`} className="lista-solicitacoes__link">
                  <span className="protocolo">{s.id}</span>
                  <span className="status-badge">{s.status}</span>
                </Link>
              </li>
            ))}
          </ul>
        )}
      </div>
    </>
  );
}
