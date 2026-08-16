import { useState } from "react";
import type { FormEvent } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import { signUp } from "../auth/authApi";
import { EntradaShell } from "./EntradaShell";

const SENHA_MIN_LENGTH = 8;

function validarSenha(senha: string): string | null {
  if (senha.length < SENHA_MIN_LENGTH) {
    return `A senha precisa ter no mínimo ${SENHA_MIN_LENGTH} caracteres.`;
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
      await signUp(email, password);
      // Conta já fica ativa no signup — sem etapa de confirmação por email (specs/client-auth).
      navigate("/login");
    } catch (err) {
      if (axios.isAxiosError(err) && err.response?.status === 409) {
        setError("Este email já está em uso. Tente entrar ou recuperar a senha.");
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
          <span className="field__hint">Mínimo {SENHA_MIN_LENGTH} caracteres.</span>
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
