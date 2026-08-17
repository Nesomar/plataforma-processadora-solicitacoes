// Mesmas regras de CpfValidator/CepValidator/TelefoneValidator (backend) — validação
// client-side é só UX (feedback antes do submit); o backend continua a fonte de verdade.

export function digitosDe(valor: string): string {
  return valor.replace(/\D/g, "");
}

export function mascaraCpf(valor: string): string {
  const d = digitosDe(valor).slice(0, 11);
  const partes = [d.slice(0, 3), d.slice(3, 6), d.slice(6, 9)].filter(Boolean);
  let resultado = partes.join(".");
  const dv = d.slice(9, 11);
  if (dv) resultado += `-${dv}`;
  return resultado;
}

export function cpfValido(valor: string): boolean {
  const d = digitosDe(valor);
  if (d.length !== 11) return false;
  if (/^(\d)\1{10}$/.test(d)) return false;

  function digitoVerificador(base: string): number {
    let soma = 0;
    let peso = base.length + 1;
    for (const c of base) {
      soma += Number(c) * peso;
      peso--;
    }
    const resto = soma % 11;
    return resto < 2 ? 0 : 11 - resto;
  }

  const d1 = digitoVerificador(d.slice(0, 9));
  const d2 = digitoVerificador(d.slice(0, 9) + d1);
  return Number(d[9]) === d1 && Number(d[10]) === d2;
}

export function mascaraCep(valor: string): string {
  const d = digitosDe(valor).slice(0, 8);
  return d.length > 5 ? `${d.slice(0, 5)}-${d.slice(5)}` : d;
}

export function cepValido(valor: string): boolean {
  return digitosDe(valor).length === 8;
}

export function mascaraTelefone(valor: string): string {
  const d = digitosDe(valor).slice(0, 11);
  if (d.length === 0) return "";
  const ddd = d.slice(0, 2);
  const resto = d.slice(2);
  if (resto.length === 0) return `(${ddd}`;
  const tamanhoParte1 = d.length <= 10 ? 4 : 5;
  const p1 = resto.slice(0, tamanhoParte1);
  const p2 = resto.slice(tamanhoParte1);
  return p2 ? `(${ddd}) ${p1}-${p2}` : `(${ddd}) ${p1}`;
}

export function telefoneValido(valor: string): boolean {
  const len = digitosDe(valor).length;
  return len === 10 || len === 11;
}

export function emailValido(valor: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valor);
}
