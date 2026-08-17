import { httpClient } from "./httpClient";
import type { DadosPessoais, Endereco, Renda } from "./perfilApi";

export interface Solicitacao {
  id: string;
  status: string;
  criadaEm: string;
  nome: string;
  cidade: string;
  uf: string;
  rendaMensal: number;
  dadosPessoais: DadosPessoais;
  endereco: Endereco;
  renda: Renda;
}

export interface AtualizarSolicitacaoBody {
  dadosPessoais: DadosPessoais;
  endereco: Endereco;
  renda: Renda;
}

export const solicitacoesApi = {
  listar: () =>
    httpClient.get<Solicitacao[]>("/api/solicitacoes").then((r) => r.data),
  criar: () =>
    httpClient.post<Solicitacao>("/api/solicitacoes").then((r) => r.data),
  buscar: (id: string) =>
    httpClient.get<Solicitacao>(`/api/solicitacoes/${id}`).then((r) => r.data),
  atualizar: (id: string, dados: AtualizarSolicitacaoBody) =>
    httpClient.patch<Solicitacao>(`/api/solicitacoes/${id}`, dados).then((r) => r.data),
};
