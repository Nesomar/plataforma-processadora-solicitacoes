import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { login } from "../auth/authApi";
import { tokenStore } from "../auth/tokenStore";
import { perfilApi } from "../api/perfilApi";
import { EntradaShell } from "./EntradaShell";

export function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setLoading(true);
    let token: string;
    try {
      token = (await login(email, password)).token;
    } catch {
      setError("Email ou senha inválidos.");
      setLoading(false);
      return;
    }
    tokenStore.set(token);
    try {
      const gate = await perfilApi.consultarGate();
      navigate(gate.completo ? "/" : "/onboarding");
    } catch {
      setError("Login feito, mas não foi possível continuar. Tente recarregar a página.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <EntradaShell
      footer={
        <p className="entrada__footer">
          Ainda não tem conta? <Link to="/cadastro">Criar conta</Link>
        </p>
      }
    >
      <h1>Entrar</h1>
      <form onSubmit={handleSubmit} className="form">
        <div className="field">
          <label className="field__label" htmlFor="login-email">
            Email
          </label>
          <input
            id="login-email"
            className="input"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="field">
          <label className="field__label" htmlFor="login-password">
            Senha
          </label>
          <input
            id="login-password"
            className="input"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        {error && (
          <p className="alert" role="alert">
            {error}
          </p>
        )}
        <button type="submit" className="button button--primary" disabled={loading}>
          {loading ? "Entrando..." : "Entrar"}
        </button>
      </form>
    </EntradaShell>
  );
}
