import { httpClient } from "./httpClient";

export interface DadosPessoais {
  nome: string;
  cpf: string;
  dataNascimento: string;
  telefone: string;
}

export interface Endereco {
  cep: string;
  logradouro: string;
  numero: string;
  complemento: string;
  bairro: string;
  cidade: string;
  uf: string;
}

export interface Renda {
  rendaMensal: number;
  ocupacao: string;
}

export type OnboardingStep = "DADOS_PESSOAIS" | "ENDERECO" | "RENDA";

export interface GateResponse {
  completo: boolean;
  proximaEtapa: OnboardingStep | null;
}

export const perfilApi = {
  consultarGate: () =>
    httpClient.get<GateResponse>("/api/perfil/gate").then((r) => r.data),
  salvarDadosPessoais: (dados: DadosPessoais) =>
    httpClient.patch("/api/perfil/dados-pessoais", dados),
  salvarEndereco: (dados: Endereco) =>
    httpClient.patch("/api/perfil/endereco", dados),
  salvarRenda: (dados: Renda) => httpClient.patch("/api/perfil/renda", dados),
};
