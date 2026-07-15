import { useEffect, useState } from "react";
import { vendas, mensagemErro } from "../api";
import type { Resumo } from "../types";
import { brl } from "../util";

export default function Relatorio() {
  const [resumo, setResumo] = useState<Resumo | null>(null);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    vendas
      .get<Resumo>("/relatorios/dia")
      .then(({ data }) => setResumo(data))
      .catch((e) => setErro(mensagemErro(e)));
  }, []);

  if (erro) return <section className="card estreito"><div className="erro">{erro}</div></section>;
  if (!resumo) return <section className="card estreito"><p className="muted">Carregando...</p></section>;

  return (
    <div className="grid-2">
      <section className="card">
        <h2>Vendas de hoje</h2>
        <div className="kpis">
          <div className="kpi"><span className="kpi-num">{resumo.quantidadeVendas}</span><span className="kpi-lbl">vendas</span></div>
          <div className="kpi"><span className="kpi-num">{brl(resumo.total)}</span><span className="kpi-lbl">faturamento</span></div>
        </div>
        <h3>Por forma de pagamento</h3>
        <table>
          <tbody>
            {Object.entries(resumo.porFormaPagamento).map(([forma, valor]) => (
              <tr key={forma}><td>{forma}</td><td className="num">{brl(valor)}</td></tr>
            ))}
            {Object.keys(resumo.porFormaPagamento).length === 0 && <tr><td className="muted">Sem vendas hoje</td></tr>}
          </tbody>
        </table>
      </section>

      <section className="card">
        <h2>Mais vendidos</h2>
        <table>
          <thead><tr><th>Produto</th><th className="num">Qtd</th><th className="num">Total</th></tr></thead>
          <tbody>
            {resumo.topProdutos.map((p) => (
              <tr key={p.produtoId}><td>{p.descricao}</td><td className="num">{p.quantidade}</td><td className="num">{brl(p.total)}</td></tr>
            ))}
            {resumo.topProdutos.length === 0 && <tr><td colSpan={3} className="muted">Nada vendido ainda</td></tr>}
          </tbody>
        </table>
      </section>
    </div>
  );
}
