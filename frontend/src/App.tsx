import { BrowserRouter, Route, Routes } from "react-router-dom";
import { ProtectedRoute } from "./auth/ProtectedRoute";
import { LoginPage } from "./pages/LoginPage";
import { SignupPage } from "./pages/SignupPage";
import { ConfirmSignUpPage } from "./pages/ConfirmSignUpPage";
import { DashboardPage } from "./pages/DashboardPage";
import { OnboardingWizard } from "./pages/onboarding/OnboardingWizard";
import { SolicitacaoDetailPage } from "./pages/SolicitacaoDetailPage";

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/cadastro" element={<SignupPage />} />
        <Route path="/cadastro/confirmar" element={<ConfirmSignUpPage />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/onboarding" element={<OnboardingWizard />} />
          <Route path="/solicitacoes/:id" element={<SolicitacaoDetailPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
