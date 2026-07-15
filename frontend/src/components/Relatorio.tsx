import { useEffect, useState } from "react";
import { vendas, mensagemErro } from "../api";
import type { Resumo } from "../types";
import { brl } from "../util";
import { IconCart, IconWallet } from "../icons";

const FORMA_LABEL: Record<string, string> = {
  DINHEIRO: "Dinheiro",
  CARTAO_CREDITO: "Cartao credito",
  CARTAO_DEBITO: "Cartao debito",
  PIX: "Pix",
};

export default function Relatorio() {
  const [resumo, setResumo] = useState<Resumo | null>(null);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    vendas
      .get<Resumo>("/relatorios/dia")
      .then(({ data }) => setResumo(data))
      .catch((e) => setErro(mensagemErro(e)));
  }, []);

  if (erro) return <section className="card card-pad"><div className="alert alert-erro">{erro}</div></section>;
  if (!resumo) return <section className="card card-pad"><p className="muted">Carregando...</p></section>;

  const formas = Object.entries(resumo.porFormaPagamento);

  return (
    <div className="stack">
      <div className="kpis" style={{ gridTemplateColumns: "1fr 1fr" }}>
        <div className="kpi">
          <span className="kpi-ico"><IconCart size={18} /></span>
          <span className="kpi-num">{resumo.quantidadeVendas}</span>
          <span className="kpi-lbl">vendas hoje</span>
        </div>
        <div className="kpi">
          <span className="kpi-ico"><IconWallet size={18} /></span>
          <span className="kpi-num">{brl(resumo.total)}</span>
          <span className="kpi-lbl">faturamento do dia</span>
        </div>
      </div>

      <div className="grid-2">
        <section className="card">
          <div className="card-head">
            <div>
              <h2>Por forma de pagamento</h2>
              <div className="sub">Distribuicao do faturamento de hoje</div>
            </div>
          </div>
          <div className="table-wrap">
            <table>
              <thead><tr><th>Forma</th><th className="right">Total</th></tr></thead>
              <tbody>
                {formas.map(([forma, valor]) => (
                  <tr key={forma}>
                    <td><span className="chip">{FORMA_LABEL[forma] ?? forma}</span></td>
                    <td className="right cell-strong">{brl(valor)}</td>
                  </tr>
                ))}
                {formas.length === 0 && <tr><td colSpan={2} className="empty">Sem vendas hoje.</td></tr>}
              </tbody>
            </table>
          </div>
        </section>

        <section className="card">
          <div className="card-head">
            <div>
              <h2>Mais vendidos</h2>
              <div className="sub">Ranking de itens por quantidade</div>
            </div>
          </div>
          <div className="table-wrap">
            <table>
              <thead><tr><th>Produto</th><th className="right">Qtd</th><th className="right">Total</th></tr></thead>
              <tbody>
                {resumo.topProdutos.map((p) => (
                  <tr key={p.produtoId}>
                    <td className="cell-strong">{p.descricao}</td>
                    <td className="right">{p.quantidade}</td>
                    <td className="right">{brl(p.total)}</td>
                  </tr>
                ))}
                {resumo.topProdutos.length === 0 && <tr><td colSpan={3} className="empty">Nada vendido ainda.</td></tr>}
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </div>
  );
}
