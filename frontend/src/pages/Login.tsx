import { useState, type FormEvent } from "react";
import { catalogo, mensagemErro } from "../api";

export default function Login({ onEntrar }: { onEntrar: (token: string) => void }) {
  const [login, setLogin] = useState("gerente");
  const [senha, setSenha] = useState("gerente123");
  const [erro, setErro] = useState<string | null>(null);
  const [carregando, setCarregando] = useState(false);

  async function submeter(e: FormEvent) {
    e.preventDefault();
    setErro(null);
    setCarregando(true);
    try {
      const { data } = await catalogo.post("/auth/login", { login, senha });
      onEntrar(data.token);
    } catch (err) {
      setErro(mensagemErro(err, "Login ou senha invalidos"));
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className="login-wrap">
      <form className="card login-card" onSubmit={submeter}>
        <h1 className="marca-titulo">Conveniencia <span>PDV</span></h1>
        <p className="muted">Entre para operar o caixa</p>
        <label>
          Usuario
          <input value={login} onChange={(e) => setLogin(e.target.value)} autoFocus />
        </label>
        <label>
          Senha
          <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} />
        </label>
        {erro && <div className="erro">{erro}</div>}
        <button className="primario" disabled={carregando}>
          {carregando ? "Entrando..." : "Entrar"}
        </button>
        <p className="dica">Demo: gerente / gerente123 ou operador / operador123</p>
      </form>
    </div>
  );
}
