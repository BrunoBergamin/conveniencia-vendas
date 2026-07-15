import { useState, type FormEvent } from "react";
import { catalogo, mensagemErro } from "../api";
import { IconStore } from "../icons";

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
    <div className="login">
      <form className="login-card" onSubmit={submeter}>
        <div className="login-brand">
          <span className="brand-mark"><IconStore size={20} /></span>
          <span className="name">Conveniencia <span className="brand-tag">PDV</span></span>
        </div>
        <div className="login-title">Entrar no sistema</div>
        <p className="login-sub">Acesse com seu usuario para operar o caixa.</p>

        <label className="field">
          <span>Usuario</span>
          <input value={login} onChange={(e) => setLogin(e.target.value)} autoFocus />
        </label>
        <label className="field">
          <span>Senha</span>
          <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} />
        </label>

        {erro && <div className="alert alert-erro">{erro}</div>}

        <button className="btn btn-primary btn-block" disabled={carregando}>
          {carregando ? "Entrando..." : "Entrar"}
        </button>

        <div className="login-hint">
          Demo: <b>gerente / gerente123</b> ou <b>operador / operador123</b>
        </div>
      </form>
    </div>
  );
}
