import { useEffect, useRef, useState } from "react";
import { catalogo, vendas, mensagemErro } from "../api";
import { FORMAS, type Produto, type Venda as VendaTipo } from "../types";
import { brl } from "../util";
import { IconPlus, IconCart, IconTrash, IconCheck } from "../icons";

type ItemCarrinho = { produto: Produto; quantidade: number };

const FORMA_LABEL: Record<string, string> = {
  DINHEIRO: "Dinheiro",
  CARTAO_CREDITO: "Cartao credito",
  CARTAO_DEBITO: "Cartao debito",
  PIX: "Pix",
};

export default function Venda() {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [selecionado, setSelecionado] = useState("");
  const [quantidade, setQuantidade] = useState("1");
  const [carrinho, setCarrinho] = useState<ItemCarrinho[]>([]);
  const [forma, setForma] = useState(FORMAS[0]);
  const [erro, setErro] = useState<string | null>(null);
  const [ultima, setUltima] = useState<VendaTipo | null>(null);
  // Chave de idempotencia da venda em andamento: se a rede falhar e o operador
  // clicar de novo, o backend reconhece a chave e NAO registra venda duplicada.
  // So trocamos a chave depois de uma venda confirmada.
  const chaveIdempotencia = useRef<string>(crypto.randomUUID());

  useEffect(() => {
    catalogo
      .get<Produto[]>("/produtos")
      .then(({ data }) => {
        setProdutos(data);
        if (data.length) setSelecionado(data[0].id);
      })
      .catch((e) => setErro(mensagemErro(e)));
  }, []);

  function adicionar() {
    const produto = produtos.find((p) => p.id === selecionado);
    const q = Number(quantidade);
    if (!produto || q <= 0) return;
    setCarrinho((c) => [...c, { produto, quantidade: q }]);
    setQuantidade("1");
  }

  function remover(indice: number) {
    setCarrinho((c) => c.filter((_, i) => i !== indice));
  }

  const total = carrinho.reduce((soma, it) => soma + it.produto.preco * it.quantidade, 0);

  async function finalizar() {
    setErro(null);
    setUltima(null);
    try {
      const body = {
        chaveIdempotencia: chaveIdempotencia.current,
        itens: carrinho.map((it) => ({ produtoId: it.produto.id, quantidade: it.quantidade })),
        formaPagamento: forma,
      };
      const { data } = await vendas.post<VendaTipo>("/vendas", body);
      setUltima(data);
      setCarrinho([]);
      chaveIdempotencia.current = crypto.randomUUID();
    } catch (e) {
      setErro(mensagemErro(e));
    }
  }

  return (
    <div className="grid-2">
      <section className="card">
        <div className="card-head">
          <div>
            <h2>Carrinho</h2>
            <div className="sub">Monte a venda e finalize no caixa aberto</div>
          </div>
        </div>

        <div className="card-pad" style={{ paddingBottom: 0 }}>
          {erro && <div className="alert alert-erro">{erro}</div>}
          <div className="field-row">
            <label className="field">
              <span>Produto</span>
              <select value={selecionado} onChange={(e) => setSelecionado(e.target.value)}>
                {produtos.map((p) => <option key={p.id} value={p.id}>{p.descricao} ({brl(p.preco)})</option>)}
              </select>
            </label>
            <label className="field narrow">
              <span>Qtd</span>
              <input type="number" min="1" value={quantidade} onChange={(e) => setQuantidade(e.target.value)} />
            </label>
            <button className="btn btn-ghost" onClick={adicionar}><IconPlus size={16} /> Add</button>
          </div>
        </div>

        <div className="table-wrap">
          <table>
            <thead><tr><th>Item</th><th className="right">Qtd</th><th className="right">Subtotal</th><th></th></tr></thead>
            <tbody>
              {carrinho.map((it, i) => (
                <tr key={i}>
                  <td className="cell-strong">{it.produto.descricao}</td>
                  <td className="right">{it.quantidade}</td>
                  <td className="right">{brl(it.produto.preco * it.quantidade)}</td>
                  <td className="right">
                    <button className="link-btn" onClick={() => remover(i)} aria-label="Remover"><IconTrash size={16} /></button>
                  </td>
                </tr>
              ))}
              {carrinho.length === 0 && <tr><td colSpan={4} className="empty">Carrinho vazio. Adicione produtos acima.</td></tr>}
            </tbody>
          </table>
        </div>

        <div className="totalbar">
          <span className="label">Total</span>
          <span className="value">{brl(total)}</span>
        </div>

        <div className="card-pad">
          <label className="field">
            <span>Forma de pagamento</span>
            <select value={forma} onChange={(e) => setForma(e.target.value)}>
              {FORMAS.map((f) => <option key={f} value={f}>{FORMA_LABEL[f] ?? f}</option>)}
            </select>
          </label>
          <button className="btn btn-primary btn-block" disabled={carrinho.length === 0} onClick={finalizar}>
            <IconCart size={16} /> Finalizar venda
          </button>
        </div>
      </section>

      <section className="card">
        <div className="card-head">
          <div>
            <h2>Ultima venda</h2>
            <div className="sub">Comprovante do registro mais recente</div>
          </div>
        </div>
        <div className="card-pad">
          {ultima ? (
            <>
              <div className="alert alert-ok"><IconCheck size={15} /> Venda registrada. Total {brl(ultima.total)}.</div>
            </>
          ) : (
            <p className="muted" style={{ marginTop: 0 }}>
              O preco vem do catalogo e o estoque baixa automaticamente ao finalizar a venda.
            </p>
          )}
        </div>
        {ultima && (
          <>
            <div className="table-wrap">
              <table>
                <thead><tr><th>Item</th><th className="right">Qtd</th><th className="right">Subtotal</th></tr></thead>
                <tbody>
                  {ultima.itens.map((it) => (
                    <tr key={it.produtoId}>
                      <td className="cell-strong">{it.descricao}</td>
                      <td className="right">{it.quantidade}</td>
                      <td className="right">{brl(it.subtotal)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="totalbar">
              <span className="label">{FORMA_LABEL[ultima.formaPagamento] ?? ultima.formaPagamento}</span>
              <span className="value">{brl(ultima.total)}</span>
            </div>
          </>
        )}
      </section>
    </div>
  );
}
