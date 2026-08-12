import type { ReactNode } from "react";

// Wrapper compartilhado do capítulo Entrada (design.md): coluna única centrada, card
// branco sem sombra. Usado por LoginPage, SignupPage e ConfirmSignUpPage.
export function EntradaShell({
  children,
  footer,
}: {
  children: ReactNode;
  footer?: ReactNode;
}) {
  return (
    <div className="shell shell--entrada">
      <div className="card">{children}</div>
      {footer}
    </div>
  );
}
