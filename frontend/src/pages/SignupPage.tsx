import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { signUp } from "../auth/cognito";
import { EntradaShell } from "./EntradaShell";

// Política do user pool (infra/terraform/modules/cognito): mínimo 8 caracteres,
// maiúscula, minúscula e número obrigatórios, símbolo opcional.
const SENHA_MIN_LENGTH = 8;
const SENHA_REGEX = {
  maiuscula: /[A-Z]/,
  minuscula: /[a-z]/,
  numero: /[0-9]/,
};

function validarSenha(senha: string): string | null {
  if (senha.length < SENHA_MIN_LENGTH) {
    return `A senha precisa ter no mínimo ${SENHA_MIN_LENGTH} caracteres.`;
  }
  if (!SENHA_REGEX.maiuscula.test(senha)) {
    return "A senha precisa ter ao menos uma letra maiúscula.";
  }
  if (!SENHA_REGEX.minuscula.test(senha)) {
    return "A senha precisa ter ao menos uma letra minúscula.";
  }
  if (!SENHA_REGEX.numero.test(senha)) {
    return "A senha precisa ter ao menos um número.";
  }
  return null;
}

export function SignupPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);

    const erroSenha = validarSenha(password);
    if (erroSenha) {
      setError(erroSenha);
      return;
    }

    setLoading(true);
    try {
      const resultado = await signUp(email, password);
      // Pool com auto-verificação pode devolver a conta já confirmada — nesse caso não
      // existe código pra digitar, então pula direto pro login (specs/client-registration).
      navigate(resultado.userConfirmed ? "/login" : "/cadastro/confirmar", {
        state: { email },
      });
    } catch (err) {
      const nome = err instanceof Error ? err.name : "";
      if (nome === "UsernameExistsException") {
        setError("Este email já está em uso. Tente entrar ou recuperar a senha.");
      } else if (nome === "InvalidPasswordException") {
        setError("Senha fora da política exigida. Tente uma senha mais forte.");
      } else {
        setError("Não foi possível criar sua conta. Tente novamente.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <EntradaShell
      footer={
        <p className="entrada__footer">
          Já tem conta? <Link to="/login">Entrar</Link>
        </p>
      }
    >
      <h1>Criar conta</h1>
      <form onSubmit={handleSubmit} className="form">
        <div className="field">
          <label className="field__label" htmlFor="signup-email">
            Email
          </label>
          <input
            id="signup-email"
            className="input"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="field">
          <label className="field__label" htmlFor="signup-password">
            Senha
          </label>
          <input
            id="signup-password"
            className="input"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <span className="field__hint">
            Mínimo 8 caracteres, com maiúscula, minúscula e número.
          </span>
        </div>
        {error && (
          <p className="alert" role="alert">
            {error}
          </p>
        )}
        <button type="submit" className="button button--primary" disabled={loading}>
          {loading ? "Criando conta..." : "Criar conta"}
        </button>
      </form>
    </EntradaShell>
  );
}
