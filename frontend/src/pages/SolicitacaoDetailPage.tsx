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

  if (error) return <p role="alert">{error}</p>;
  if (!solicitacao) return <p>Carregando...</p>;

  return (
    <div>
      <h1>Solicitação {solicitacao.id}</h1>
      <p>Status: {solicitacao.status}</p>
      <p>Criada em: {new Date(solicitacao.criadaEm).toLocaleString("pt-BR")}</p>
      <p>Nome: {solicitacao.nome}</p>
      <p>Cidade/UF: {solicitacao.cidade}/{solicitacao.uf}</p>
      <p>Renda mensal: {solicitacao.rendaMensal}</p>
    </div>
  );
}
