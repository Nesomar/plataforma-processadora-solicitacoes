import { BrowserRouter, Route, Routes } from "react-router-dom";
import { ProtectedRoute } from "./auth/ProtectedRoute";
import { LoginPage } from "./pages/LoginPage";
import { SignupPage } from "./pages/SignupPage";
import { DashboardPage } from "./pages/DashboardPage";
import { OnboardingWizard } from "./pages/onboarding/OnboardingWizard";
import { SolicitacaoDetailPage } from "./pages/SolicitacaoDetailPage";
import { AnexosPage } from "./pages/AnexosPage";

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/cadastro" element={<SignupPage />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/onboarding" element={<OnboardingWizard />} />
          <Route path="/solicitacoes/:id" element={<SolicitacaoDetailPage />} />
          <Route path="/anexos" element={<AnexosPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
