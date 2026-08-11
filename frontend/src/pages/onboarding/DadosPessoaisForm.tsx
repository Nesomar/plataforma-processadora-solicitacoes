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
    <form onSubmit={handleSubmit}>
      <h2>Dados pessoais</h2>
      <label>
        Nome
        <input
          value={form.nome}
          onChange={(e) => setForm({ ...form, nome: e.target.value })}
          required
        />
      </label>
      <label>
        CPF
        <input
          value={form.cpf}
          onChange={(e) => setForm({ ...form, cpf: e.target.value })}
          required
        />
      </label>
      <label>
        Data de nascimento
        <input
          type="date"
          value={form.dataNascimento}
          onChange={(e) => setForm({ ...form, dataNascimento: e.target.value })}
          required
        />
      </label>
      <label>
        Telefone
        <input
          value={form.telefone}
          onChange={(e) => setForm({ ...form, telefone: e.target.value })}
          required
        />
      </label>
      <button type="submit">Continuar</button>
    </form>
  );
}
