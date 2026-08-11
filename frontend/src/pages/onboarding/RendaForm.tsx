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
    <form onSubmit={handleSubmit}>
      <h2>Renda</h2>
      <label>
        Renda mensal
        <input
          type="number"
          step="0.01"
          min="0"
          value={rendaMensal}
          onChange={(e) => setRendaMensal(e.target.value)}
          required
        />
      </label>
      <label>
        Ocupação
        <input
          value={ocupacao}
          onChange={(e) => setOcupacao(e.target.value)}
          required
        />
      </label>
      <button type="submit">Concluir</button>
    </form>
  );
}
