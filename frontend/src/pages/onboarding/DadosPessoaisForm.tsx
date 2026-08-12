import { useState } from "react";
import type { FormEvent } from "react";
import type { DadosPessoais } from "../../api/perfilApi";

export function DadosPessoaisForm({
  onSubmit,
}: {
  onSubmit: (dados: DadosPessoais) => Promise<void>;
}) {
  const [form, setForm] = useState<DadosPessoais>({
    nome: "",
    cpf: "",
    dataNascimento: "",
    telefone: "",
  });

  function handleSubmit(event: FormEvent) {
    event.preventDefault();
    void onSubmit(form);
  }

  return (
    <form onSubmit={handleSubmit} className="form">
      <h2>Dados pessoais</h2>
      <div className="field">
        <label className="field__label" htmlFor="dp-nome">
          Nome
        </label>
        <input
          id="dp-nome"
          className="input"
          value={form.nome}
          onChange={(e) => setForm({ ...form, nome: e.target.value })}
          required
        />
      </div>
      <div className="field">
        <label className="field__label" htmlFor="dp-cpf">
          CPF
        </label>
        <input
          id="dp-cpf"
          className="input input--mono"
          value={form.cpf}
          onChange={(e) => setForm({ ...form, cpf: e.target.value })}
          required
        />
      </div>
      <div className="field">
        <label className="field__label" htmlFor="dp-nascimento">
          Data de nascimento
        </label>
        <input
          id="dp-nascimento"
          className="input"
          type="date"
          value={form.dataNascimento}
          onChange={(e) => setForm({ ...form, dataNascimento: e.target.value })}
          required
        />
      </div>
      <div className="field">
        <label className="field__label" htmlFor="dp-telefone">
          Telefone
        </label>
        <input
          id="dp-telefone"
          className="input"
          value={form.telefone}
          onChange={(e) => setForm({ ...form, telefone: e.target.value })}
          required
        />
      </div>
      <button type="submit" className="button button--primary">
        Continuar
      </button>
    </form>
  );
}
