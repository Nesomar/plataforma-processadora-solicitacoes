import { useState } from "react";
import type { FormEvent } from "react";
import type { Endereco } from "../../api/perfilApi";
import { cepValido, mascaraCep } from "../../utils/validacao";

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
  const [erroCep, setErroCep] = useState<string | null>(null);

  function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!cepValido(form.cep)) {
      setErroCep("CEP inválido.");
      return;
    }
    setErroCep(null);
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
          onChange={(e) =>
            setForm({ ...form, [key]: key === "cep" ? mascaraCep(e.target.value) : e.target.value })
          }
          inputMode={key === "cep" ? "numeric" : undefined}
          required={required}
        />
        {key === "cep" && erroCep && (
          <span className="field__error" role="alert">
            {erroCep}
          </span>
        )}
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
