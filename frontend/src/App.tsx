import { useEffect, useState } from "react";
import { getToken, registrarLogout, setToken } from "./api";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";

export default function App() {
  const [autenticado, setAutenticado] = useState<boolean>(!!getToken());

  useEffect(() => {
    // Se qualquer chamada devolver 401, desloga automaticamente.
    registrarLogout(() => {
      setToken(null);
      setAutenticado(false);
    });
  }, []);

  function entrar(token: string) {
    setToken(token);
    setAutenticado(true);
  }

  function sair() {
    setToken(null);
    setAutenticado(false);
  }

  return autenticado ? <Dashboard onSair={sair} /> : <Login onEntrar={entrar} />;
}
