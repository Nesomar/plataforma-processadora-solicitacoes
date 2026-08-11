import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import type {
  DadosPessoais,
  Endereco,
  OnboardingStep,
  Renda,
} from "../../api/perfilApi";
import { perfilApi } from "../../api/perfilApi";
import { DadosPessoaisForm } from "./DadosPessoaisForm";
import { EnderecoForm } from "./EnderecoForm";
import { RendaForm } from "./RendaForm";

// Etapa exibida vem sempre do gate do backend (não de estado local do front) — é o
// backend que decide onde retomar (specs/client-profile/spec.md).
export function OnboardingWizard() {
  const [step, setStep] = useState<OnboardingStep | null | "loading">(
    "loading",
  );
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const submitting = useRef(false);

  useEffect(() => {
    void refreshGate().catch(() =>
      setError("Não foi possível carregar seu cadastro. Recarregue a página."),
    );
  }, []);

  async function refreshGate() {
    const gate = await perfilApi.consultarGate();
    if (gate.completo) {
      navigate("/", { replace: true });
      return;
    }
    setStep(gate.proximaEtapa);
  }

  // Evita double-submit (clique duplo) mandar dois PATCHes concorrentes pro mesmo
  // clienteId — o backend não faz locking otimista (ver PerfilService).
  async function handleSubmit(save: () => Promise<unknown>) {
    if (submitting.current) return;
    submitting.current = true;
    setError(null);
    try {
      await save();
      await refreshGate();
    } catch {
      setError("Não foi possível salvar. Tente novamente.");
    } finally {
      submitting.current = false;
    }
  }

  if (step === "loading") return <p role="alert">{error ?? "Carregando..."}</p>;

  return (
    <div>
      {error && <p role="alert">{error}</p>}
      {step === "DADOS_PESSOAIS" && (
        <DadosPessoaisForm
          onSubmit={(dados: DadosPessoais) =>
            handleSubmit(() => perfilApi.salvarDadosPessoais(dados))
          }
        />
      )}
      {step === "ENDERECO" && (
        <EnderecoForm
          onSubmit={(dados: Endereco) =>
            handleSubmit(() => perfilApi.salvarEndereco(dados))
          }
        />
      )}
      {step === "RENDA" && (
        <RendaForm
          onSubmit={(dados: Renda) =>
            handleSubmit(() => perfilApi.salvarRenda(dados))
          }
        />
      )}
    </div>
  );
}
