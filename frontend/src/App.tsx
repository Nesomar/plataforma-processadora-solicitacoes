import { BrowserRouter, Route, Routes } from "react-router-dom";
import { ProtectedRoute } from "./auth/ProtectedRoute";
import { LoginPage } from "./pages/LoginPage";
import { DashboardPage } from "./pages/DashboardPage";
import { OnboardingWizard } from "./pages/onboarding/OnboardingWizard";

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/onboarding" element={<OnboardingWizard />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
