import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Link, useParams } from "react-router-dom";
import type { DadosPessoais, Endereco, Renda } from "../api/perfilApi";
import type { Solicitacao } from "../api/solicitacoesApi";
import { solicitacoesApi } from "../api/solicitacoesApi";
import { cepValido, cpfValido, mascaraCep, mascaraCpf, mascaraTelefone, telefoneValido } from "../utils/validacao";

type FormEdicao = {
  dadosPessoais: DadosPessoais;
  endereco: Endereco;
  renda: Renda;
};

function paraForm(s: Solicitacao): FormEdicao {
  return { dadosPessoais: s.dadosPessoais, endereco: s.endereco, renda: s.renda };
}

export function SolicitacaoDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [solicitacao, setSolicitacao] = useState<Solicitacao | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [editando, setEditando] = useState(false);
  const [form, setForm] = useState<FormEdicao | null>(null);
  const [erros, setErros] = useState<{ cpf?: string; cep?: string; telefone?: string }>({});
  const [salvando, setSalvando] = useState(false);
  const [salvo, setSalvo] = useState(false);

  useEffect(() => {
    if (!id) return;
    solicitacoesApi
      .buscar(id)
      .then(setSolicitacao)
      .catch(() => setError("Solicitação não encontrada."));
  }, [id]);

  function iniciarEdicao() {
    if (!solicitacao) return;
    setForm(paraForm(solicitacao));
    setErros({});
    setSalvo(false);
    setEditando(true);
  }

  async function handleSalvar(event: FormEvent) {
    event.preventDefault();
    if (!id || !form) return;

    const novosErros: typeof erros = {};
    if (!cpfValido(form.dadosPessoais.cpf)) novosErros.cpf = "CPF inválido.";
    if (!cepValido(form.endereco.cep)) novosErros.cep = "CEP inválido.";
    if (!telefoneValido(form.dadosPessoais.telefone)) novosErros.telefone = "Telefone inválido — informe DDD + número.";
    setErros(novosErros);
    if (Object.keys(novosErros).length > 0) return;

    setSalvando(true);
    setError(null);
    try {
      const atualizada = await solicitacoesApi.atualizar(id, form);
      setSolicitacao(atualizada);
      setEditando(false);
      setSalvo(true);
    } catch {
      setError("Não foi possível salvar as alterações. Tente novamente.");
    } finally {
      setSalvando(false);
    }
  }

  return (
    <>
      <header className="topbar">
        <span className="topbar__brand">Portal do Cliente</span>
        {solicitacao && <span className="protocolo">{solicitacao.id}</span>}
      </header>
      <div className="shell shell--painel">
        <Link to="/" className="button button--secondary">
          ← Voltar
        </Link>
        {error && (
          <p className="alert" role="alert">
            {error}
          </p>
        )}
        {!error && !solicitacao && <p className="loading">Carregando...</p>}
        {solicitacao && !editando && (
          <div className="card">
            <div className="painel__header">
              <h1>Solicitação</h1>
              {solicitacao.status === "ABERTA" && (
                <button type="button" className="button button--primary" onClick={iniciarEdicao}>
                  Editar
                </button>
              )}
            </div>
            {salvo && (
              <p className="confirm" role="status">
                Alterações salvas.
              </p>
            )}
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
        {solicitacao && editando && form && (
          <form onSubmit={handleSalvar} className="form card">
            <h1>Editar solicitação</h1>
            <div className="field">
              <label className="field__label" htmlFor="edit-nome">
                Nome
              </label>
              <input
                id="edit-nome"
                className="input"
                value={form.dadosPessoais.nome}
                onChange={(e) => setForm({ ...form, dadosPessoais: { ...form.dadosPessoais, nome: e.target.value } })}
                required
              />
            </div>
            <div className="field">
              <label className="field__label" htmlFor="edit-cpf">
                CPF
              </label>
              <input
                id="edit-cpf"
                className="input input--mono"
                value={form.dadosPessoais.cpf}
                onChange={(e) =>
                  setForm({ ...form, dadosPessoais: { ...form.dadosPessoais, cpf: mascaraCpf(e.target.value) } })
                }
                inputMode="numeric"
                required
              />
              {erros.cpf && (
                <span className="field__error" role="alert">
                  {erros.cpf}
                </span>
              )}
            </div>
            <div className="field">
              <label className="field__label" htmlFor="edit-nascimento">
                Data de nascimento
              </label>
              <input
                id="edit-nascimento"
                className="input"
                type="date"
                value={form.dadosPessoais.dataNascimento}
                onChange={(e) =>
                  setForm({ ...form, dadosPessoais: { ...form.dadosPessoais, dataNascimento: e.target.value } })
                }
                required
              />
            </div>
            <div className="field">
              <label className="field__label" htmlFor="edit-telefone">
                Telefone
              </label>
              <input
                id="edit-telefone"
                className="input"
                value={form.dadosPessoais.telefone}
                onChange={(e) =>
                  setForm({
                    ...form,
                    dadosPessoais: { ...form.dadosPessoais, telefone: mascaraTelefone(e.target.value) },
                  })
                }
                inputMode="numeric"
                required
              />
              {erros.telefone && (
                <span className="field__error" role="alert">
                  {erros.telefone}
                </span>
              )}
            </div>
            <div className="field">
              <label className="field__label" htmlFor="edit-cep">
                CEP
              </label>
              <input
                id="edit-cep"
                className="input"
                value={form.endereco.cep}
                onChange={(e) => setForm({ ...form, endereco: { ...form.endereco, cep: mascaraCep(e.target.value) } })}
                inputMode="numeric"
                required
              />
              {erros.cep && (
                <span className="field__error" role="alert">
                  {erros.cep}
                </span>
              )}
            </div>
            <div className="field">
              <label className="field__label" htmlFor="edit-logradouro">
                Logradouro
              </label>
              <input
                id="edit-logradouro"
                className="input"
                value={form.endereco.logradouro}
                onChange={(e) => setForm({ ...form, endereco: { ...form.endereco, logradouro: e.target.value } })}
                required
              />
            </div>
            <div className="field">
              <label className="field__label" htmlFor="edit-numero">
                Número
              </label>
              <input
                id="edit-numero"
                className="input"
                value={form.endereco.numero}
                onChange={(e) => setForm({ ...form, endereco: { ...form.endereco, numero: e.target.value } })}
                required
              />
            </div>
            <div className="field">
              <label className="field__label" htmlFor="edit-complemento">
                Complemento
              </label>
              <input
                id="edit-complemento"
                className="input"
                value={form.endereco.complemento}
                onChange={(e) => setForm({ ...form, endereco: { ...form.endereco, complemento: e.target.value } })}
              />
            </div>
            <div className="field">
              <label className="field__label" htmlFor="edit-bairro">
                Bairro
              </label>
              <input
                id="edit-bairro"
                className="input"
                value={form.endereco.bairro}
                onChange={(e) => setForm({ ...form, endereco: { ...form.endereco, bairro: e.target.value } })}
                required
              />
            </div>
            <div className="field">
              <label className="field__label" htmlFor="edit-cidade">
                Cidade
              </label>
              <input
                id="edit-cidade"
                className="input"
                value={form.endereco.cidade}
                onChange={(e) => setForm({ ...form, endereco: { ...form.endereco, cidade: e.target.value } })}
                required
              />
            </div>
            <div className="field">
              <label className="field__label" htmlFor="edit-uf">
                UF
              </label>
              <input
                id="edit-uf"
                className="input"
                value={form.endereco.uf}
                onChange={(e) => setForm({ ...form, endereco: { ...form.endereco, uf: e.target.value } })}
                required
              />
            </div>
            <div className="field">
              <label className="field__label" htmlFor="edit-renda">
                Renda mensal
              </label>
              <input
                id="edit-renda"
                className="input input--mono"
                type="number"
                step="0.01"
                min="0"
                value={form.renda.rendaMensal}
                onChange={(e) => setForm({ ...form, renda: { ...form.renda, rendaMensal: Number(e.target.value) } })}
                required
              />
            </div>
            <div className="field">
              <label className="field__label" htmlFor="edit-ocupacao">
                Ocupação
              </label>
              <input
                id="edit-ocupacao"
                className="input"
                value={form.renda.ocupacao}
                onChange={(e) => setForm({ ...form, renda: { ...form.renda, ocupacao: e.target.value } })}
                required
              />
            </div>
            <div className="painel__header">
              <button type="button" className="button button--secondary" onClick={() => setEditando(false)}>
                Cancelar
              </button>
              <button type="submit" className="button button--primary" disabled={salvando}>
                {salvando ? "Salvando..." : "Salvar"}
              </button>
            </div>
          </form>
        )}
      </div>
    </>
  );
}
