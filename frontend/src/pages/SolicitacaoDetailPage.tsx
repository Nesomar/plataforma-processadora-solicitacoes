import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import type { Solicitacao } from "../api/solicitacoesApi";
import { solicitacoesApi } from "../api/solicitacoesApi";

export function SolicitacaoDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [solicitacao, setSolicitacao] = useState<Solicitacao | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    solicitacoesApi
      .buscar(id)
      .then(setSolicitacao)
      .catch(() => setError("Solicitação não encontrada."));
  }, [id]);

  return (
    <>
      <header className="topbar">
        <span className="topbar__brand">Portal do Cliente</span>
        {solicitacao && <span className="protocolo">{solicitacao.id}</span>}
      </header>
      <div className="shell shell--painel">
        {error && (
          <p className="alert" role="alert">
            {error}
          </p>
        )}
        {!error && !solicitacao && <p className="loading">Carregando...</p>}
        {solicitacao && (
          <div className="card">
            <h1>Solicitação</h1>
            <div className="detalhe-grid">
              <div className="detalhe-grid__row">
                <span className="detalhe-grid__label">Status</span>
                <span className="detalhe-grid__value status-badge" data-status={solicitacao.status}>
                  {solicitacao.status}
                </span>
              </div>
              <div className="detalhe-grid__row">
                <span className="detalhe-grid__label">Criada em</span>
                <span className="detalhe-grid__value mono">
                  {new Date(solicitacao.criadaEm).toLocaleString("pt-BR")}
                </span>
              </div>
              <div className="detalhe-grid__row">
                <span className="detalhe-grid__label">Nome</span>
                <span className="detalhe-grid__value">{solicitacao.nome}</span>
              </div>
              <div className="detalhe-grid__row">
                <span className="detalhe-grid__label">Cidade/UF</span>
                <span className="detalhe-grid__value">
                  {solicitacao.cidade}/{solicitacao.uf}
                </span>
              </div>
              <div className="detalhe-grid__row">
                <span className="detalhe-grid__label">Renda mensal</span>
                <span className="detalhe-grid__value mono">{solicitacao.rendaMensal}</span>
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  );
}
