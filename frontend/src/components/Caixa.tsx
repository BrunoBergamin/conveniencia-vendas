import { useEffect, useState } from "react";
import { vendas, mensagemErro } from "../api";
import type { Caixa as CaixaTipo } from "../types";
import { brl } from "../util";

export default function Caixa() {
  const [caixa, setCaixa] = useState<CaixaTipo | null>(null);
  const [fundo, setFundo] = useState("100.00");
  const [erro, setErro] = useState<string | null>(null);
  const [msg, setMsg] = useState<string | null>(null);

  async function carregar() {
    try {
      const resp = await vendas.get("/caixas/aberto", {
        validateStatus: (s) => s === 200 || s === 204,
      });
      setCaixa(resp.status === 204 ? null : (resp.data as CaixaTipo));
    } catch (e) {
      setErro(mensagemErro(e));
    }
  }

  useEffect(() => {
    carregar();
  }, []);

  async function abrir() {
    setErro(null);
    setMsg(null);
    try {
      await vendas.post("/caixas", { fundoTroco: Number(fundo) });
      setMsg("Caixa aberto.");
      await carregar();
    } catch (e) {
      setErro(mensagemErro(e));
    }
  }

  async function fechar() {
    if (!caixa) return;
    setErro(null);
    setMsg(null);
    try {
      await vendas.post(`/caixas/${caixa.id}/fechar`);
      setMsg("Caixa fechado.");
      await carregar();
    } catch (e) {
      setErro(mensagemErro(e));
    }
  }

  return (
    <section className="card estreito">
      <h2>Caixa</h2>
      {erro && <div className="erro">{erro}</div>}
      {msg && <div className="ok">{msg}</div>}

      {caixa ? (
        <>
          <div className="status-linha">
            <span className="chip verde">ABERTO</span>
            <span className="muted">operador {caixa.operador}</span>
          </div>
          <dl className="ficha">
            <div><dt>Fundo de troco</dt><dd>{brl(caixa.fundoTroco)}</dd></div>
            <div><dt>Aberto em</dt><dd>{new Date(caixa.aberturaEm).toLocaleString("pt-BR")}</dd></div>
          </dl>
          <button className="perigo" onClick={fechar}>Fechar caixa</button>
        </>
      ) : (
        <>
          <p className="muted">Nao ha caixa aberto. Abra para comecar a vender.</p>
          <label>Fundo de troco<input type="number" step="0.01" min="0" value={fundo} onChange={(e) => setFundo(e.target.value)} /></label>
          <button className="primario" onClick={abrir}>Abrir caixa</button>
        </>
      )}
    </section>
  );
}
