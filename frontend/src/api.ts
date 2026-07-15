import axios, { type AxiosInstance } from "axios";

const catalogoURL = import.meta.env.VITE_CATALOGO_URL ?? "http://localhost:8081";
const vendasURL = import.meta.env.VITE_VENDAS_URL ?? "http://localhost:8082";

let token: string | null = localStorage.getItem("token");
let aoDeslogar: (() => void) | null = null;

export function setToken(valor: string | null) {
  token = valor;
  if (valor) localStorage.setItem("token", valor);
  else localStorage.removeItem("token");
}

export function getToken(): string | null {
  return token;
}

/** Le login e papel de dentro do JWT, sem chamar a API. */
export function getUsuario(): { login: string; papel: string } | null {
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return { login: payload.sub ?? "usuario", papel: payload.papel ?? "OPERADOR" };
  } catch {
    return null;
  }
}

export function registrarLogout(fn: () => void) {
  aoDeslogar = fn;
}

function criar(baseURL: string): AxiosInstance {
  const instancia = axios.create({ baseURL });
  instancia.interceptors.request.use((config) => {
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  });
  instancia.interceptors.response.use(
    (r) => r,
    (erro) => {
      if (erro?.response?.status === 401 && aoDeslogar) aoDeslogar();
      return Promise.reject(erro);
    },
  );
  return instancia;
}

export const catalogo = criar(catalogoURL);
export const vendas = criar(vendasURL);

/** Extrai uma mensagem amigavel do erro da API. */
export function mensagemErro(erro: unknown, padrao = "Algo deu errado"): string {
  if (axios.isAxiosError(erro)) {
    return erro.response?.data?.mensagem ?? erro.message ?? padrao;
  }
  return padrao;
}
