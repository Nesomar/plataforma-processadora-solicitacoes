import { httpClient } from "./httpClient";

export interface Solicitacao {
  id: string;
  status: string;
  criadaEm: string;
  nome: string;
  cidade: string;
  uf: string;
  rendaMensal: number;
}

export const solicitacoesApi = {
  listar: () =>
    httpClient.get<Solicitacao[]>("/api/solicitacoes").then((r) => r.data),
  criar: () =>
    httpClient.post<Solicitacao>("/api/solicitacoes").then((r) => r.data),
  buscar: (id: string) =>
    httpClient.get<Solicitacao>(`/api/solicitacoes/${id}`).then((r) => r.data),
};
