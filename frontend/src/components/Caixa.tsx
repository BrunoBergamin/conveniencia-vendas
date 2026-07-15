import { useEffect, useState } from "react";
import { vendas, mensagemErro } from "../api";
import type { Caixa as CaixaTipo } from "../types";
import { brl } from "../util";
import { IconWallet, IconCheck } from "../icons";

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
      setMsg("Caixa aberto com sucesso.");
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
    <div style={{ maxWidth: 460 }}>
      <section className="card">
        <div className="card-head">
          <div>
            <h2>Turno do caixa</h2>
            <div className="sub">Controle de abertura e fechamento</div>
          </div>
          <span className={"pill " + (caixa ? "pill-ok" : "pill-off")}>
            {caixa ? "Aberto" : "Fechado"}
          </span>
        </div>

        <div className="card-pad">
          {erro && <div className="alert alert-erro">{erro}</div>}
          {msg && <div className="alert alert-ok"><IconCheck size={15} /> {msg}</div>}

          {caixa ? (
            <>
              <dl className="ficha">
                <div className="ficha-row"><dt>Operador</dt><dd>{caixa.operador}</dd></div>
                <div className="ficha-row"><dt>Fundo de troco</dt><dd>{brl(caixa.fundoTroco)}</dd></div>
                <div className="ficha-row"><dt>Aberto em</dt><dd>{new Date(caixa.aberturaEm).toLocaleString("pt-BR")}</dd></div>
              </dl>
              <button className="btn btn-danger btn-block" style={{ marginTop: 16 }} onClick={fechar}>
                Fechar caixa
              </button>
            </>
          ) : (
            <>
              <p className="muted" style={{ marginTop: 0 }}>
                Nao ha caixa aberto. Informe o fundo de troco e abra o turno para comecar a vender.
              </p>
              <label className="field">
                <span>Fundo de troco</span>
                <input type="number" step="0.01" min="0" value={fundo} onChange={(e) => setFundo(e.target.value)} />
              </label>
              <button className="btn btn-primary btn-block" onClick={abrir}>
                <IconWallet size={16} /> Abrir caixa
              </button>
            </>
          )}
        </div>
      </section>
    </div>
  );
}
