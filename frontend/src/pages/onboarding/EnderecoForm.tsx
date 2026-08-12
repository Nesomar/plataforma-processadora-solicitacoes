import { useState } from "react";
import type { FormEvent } from "react";
import type { Endereco } from "../../api/perfilApi";

export function EnderecoForm({
  onSubmit,
}: {
  onSubmit: (dados: Endereco) => Promise<void>;
}) {
  const [form, setForm] = useState<Endereco>({
    cep: "",
    logradouro: "",
    numero: "",
    complemento: "",
    bairro: "",
    cidade: "",
    uf: "",
  });

  function handleSubmit(event: FormEvent) {
    event.preventDefault();
    void onSubmit(form);
  }

  function field(key: keyof Endereco, label: string, required = true) {
    const id = `end-${key}`;
    return (
      <div className="field">
        <label className="field__label" htmlFor={id}>
          {label}
        </label>
        <input
          id={id}
          className="input"
          value={form[key]}
          onChange={(e) => setForm({ ...form, [key]: e.target.value })}
          required={required}
        />
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} className="form">
      <h2>Endereço</h2>
      {field("cep", "CEP")}
      {field("logradouro", "Logradouro")}
      {field("numero", "Número")}
      {field("complemento", "Complemento", false)}
      {field("bairro", "Bairro")}
      {field("cidade", "Cidade")}
      {field("uf", "UF")}
      <button type="submit" className="button button--primary">
        Continuar
      </button>
    </form>
  );
}
