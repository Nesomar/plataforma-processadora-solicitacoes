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
import { AnexosStep } from "./AnexosStep";

type WizardStep = OnboardingStep | "ANEXOS" | "loading" | null;

const ETAPAS: { step: OnboardingStep | "ANEXOS"; label: string }[] = [
  { step: "DADOS_PESSOAIS", label: "Dados pessoais" },
  { step: "ENDERECO", label: "Endereço" },
  { step: "RENDA", label: "Renda" },
  { step: "ANEXOS", label: "Anexos" },
];

// Etapa exibida vem sempre do gate do backend (não de estado local do front) — é o
// backend que decide onde retomar (specs/client-profile/spec.md). Anexos é etapa só do
// front: o gate de perfil (backend) não depende dela.
export function OnboardingWizard() {
  const [step, setStep] = useState<WizardStep>("loading");
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
      setStep("ANEXOS");
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

  if (step === "loading") {
    return <p className="loading">{error ?? "Carregando..."}</p>;
  }

  const indiceAtual = ETAPAS.findIndex((e) => e.step === step);

  return (
    <div className="shell shell--dossie">
      <div className="progress-rail">
        {ETAPAS.map((etapa, i) => (
          <div
            key={etapa.step}
            className={`progress-step ${
              i < indiceAtual ? "progress-step--done" : i === indiceAtual ? "progress-step--active" : ""
            }`}
          >
            <div className="progress-step__bar" />
            <span className="progress-step__label">{etapa.label}</span>
          </div>
        ))}
      </div>
      <div className="card step-enter" data-etapa={step} key={step}>
        {error && (
          <p className="alert" role="alert">
            {error}
          </p>
        )}
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
        {step === "ANEXOS" && (
          <AnexosStep onContinuar={() => navigate("/", { replace: true })} />
        )}
      </div>
    </div>
  );
}
