import { useState } from "react";
import type { FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../auth/cognito";
import { tokenStore } from "../auth/tokenStore";
import { perfilApi } from "../api/perfilApi";

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
    let idToken: string;
    try {
      idToken = (await login(email, password)).idToken;
    } catch {
      setError("Email ou senha inválidos.");
      setLoading(false);
      return;
    }
    tokenStore.set(idToken);
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
    <form onSubmit={handleSubmit}>
      <h1>Entrar</h1>
      <label>
        Email
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
      </label>
      <label>
        Senha
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
      </label>
      {error && <p role="alert">{error}</p>}
      <button type="submit" disabled={loading}>
        {loading ? "Entrando..." : "Entrar"}
      </button>
    </form>
  );
}
