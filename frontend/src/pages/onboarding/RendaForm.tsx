import { useState } from "react";
import type { FormEvent } from "react";
import type { Renda } from "../../api/perfilApi";

export function RendaForm({
  onSubmit,
}: {
  onSubmit: (dados: Renda) => Promise<void>;
}) {
  const [rendaMensal, setRendaMensal] = useState("");
  const [ocupacao, setOcupacao] = useState("");

  function handleSubmit(event: FormEvent) {
    event.preventDefault();
    void onSubmit({ rendaMensal: Number(rendaMensal), ocupacao });
  }

  return (
    <form onSubmit={handleSubmit} className="form">
      <h2>Renda</h2>
      <div className="field">
        <label className="field__label" htmlFor="renda-mensal">
          Renda mensal
        </label>
        <input
          id="renda-mensal"
          className="input input--mono"
          type="number"
          step="0.01"
          min="0"
          value={rendaMensal}
          onChange={(e) => setRendaMensal(e.target.value)}
          required
        />
      </div>
      <div className="field">
        <label className="field__label" htmlFor="renda-ocupacao">
          Ocupação
        </label>
        <input
          id="renda-ocupacao"
          className="input"
          value={ocupacao}
          onChange={(e) => setOcupacao(e.target.value)}
          required
        />
      </div>
      <button type="submit" className="button button--primary">
        Concluir
      </button>
    </form>
  );
}
