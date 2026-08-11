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
    <div>
      <h1>Minhas solicitações</h1>
      <button type="button" onClick={novaSolicitacao} disabled={criando}>
        {criando ? "Criando..." : "Nova solicitação"}
      </button>
      {error && <p role="alert">{error}</p>}
      {solicitacoes && solicitacoes.length === 0 && <p>Nenhuma solicitação ainda.</p>}
      <ul>
        {solicitacoes?.map((s) => (
          <li key={s.id}>
            <Link to={`/solicitacoes/${s.id}`}>
              {s.id} — {s.status}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
