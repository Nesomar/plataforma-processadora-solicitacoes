import { useState } from "react";
import type { FormEvent } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { confirmSignUp } from "../auth/cognito";
import { EntradaShell } from "./EntradaShell";

// Email chega via location.state (D4 do design.md). Se o cliente recarregar a página e
// perder o state, pedimos o email de novo em vez de bloquear a confirmação.
export function ConfirmSignUpPage() {
  const location = useLocation();
  const emailInicial = (location.state as { email?: string } | null)?.email ?? "";
  const [email, setEmail] = useState(emailInicial);
  const [code, setCode] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [confirmado, setConfirmado] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await confirmSignUp(email, code);
      setConfirmado(true);
    } catch (err) {
      const nome = err instanceof Error ? err.name : "";
      if (nome === "CodeMismatchException") {
        setError("Código incorreto. Confira o email e tente novamente.");
      } else if (nome === "ExpiredCodeException") {
        setError("Código expirado. Solicite um novo cadastro para receber outro.");
      } else {
        setError("Não foi possível confirmar sua conta. Tente novamente.");
      }
    } finally {
      setLoading(false);
    }
  }

  if (confirmado) {
    return (
      <EntradaShell>
        <h1>Conta confirmada</h1>
        <p className="confirm" role="status">
          Sua conta foi confirmada. Você já pode entrar.
        </p>
        <button type="button" className="button button--primary" onClick={() => navigate("/login")}>
          Ir para o login
        </button>
      </EntradaShell>
    );
  }

  return (
    <EntradaShell>
      <h1>Confirmar conta</h1>
      <form onSubmit={handleSubmit} className="form">
        {!emailInicial && (
          <div className="field">
            <label className="field__label" htmlFor="confirm-email">
              Email
            </label>
            <input
              id="confirm-email"
              className="input"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
        )}
        <div className="field">
          <label className="field__label" htmlFor="confirm-code">
            Código de confirmação
          </label>
          <input
            id="confirm-code"
            className="input input--mono"
            inputMode="numeric"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            required
          />
          <span className="field__hint">Enviamos um código para {email || "seu email"}.</span>
        </div>
        {error && (
          <p className="alert" role="alert">
            {error}
          </p>
        )}
        <button type="submit" className="button button--primary" disabled={loading}>
          {loading ? "Confirmando..." : "Confirmar"}
        </button>
      </form>
    </EntradaShell>
  );
}
