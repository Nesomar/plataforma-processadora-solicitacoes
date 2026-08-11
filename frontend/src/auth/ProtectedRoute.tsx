import { Navigate, Outlet } from "react-router-dom";
import { tokenStore } from "./tokenStore";

export function ProtectedRoute() {
  return tokenStore.get() ? <Outlet /> : <Navigate to="/login" replace />;
}
