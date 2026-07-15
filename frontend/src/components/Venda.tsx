import { useEffect, useState } from "react";
import { catalogo, vendas, mensagemErro } from "../api";
import { FORMAS, type Produto, type Venda as VendaTipo } from "../types";
import { brl } from "../util";

type ItemCarrinho = { produto: Produto; quantidade: number };

export default function Venda() {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [selecionado, setSelecionado] = useState("");
  const [quantidade, setQuantidade] = useState("1");
  const [carrinho, setCarrinho] = useState<ItemCarrinho[]>([]);
  const [forma, setForma] = useState(FORMAS[0]);
  const [erro, setErro] = useState<string | null>(null);
  const [ultima, setUltima] = useState<VendaTipo | null>(null);

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
        itens: carrinho.map((it) => ({ produtoId: it.produto.id, quantidade: it.quantidade })),
        formaPagamento: forma,
      };
      const { data } = await vendas.post<VendaTipo>("/vendas", body);
      setUltima(data);
      setCarrinho([]);
    } catch (e) {
      setErro(mensagemErro(e));
    }
  }

  return (
    <div className="grid-2">
      <section className="card">
        <h2>Nova venda</h2>
        {erro && <div className="erro">{erro}</div>}
        <div className="form-linha">
          <label className="cresce">Produto
            <select value={selecionado} onChange={(e) => setSelecionado(e.target.value)}>
              {produtos.map((p) => <option key={p.id} value={p.id}>{p.descricao} ({brl(p.preco)})</option>)}
            </select>
          </label>
          <label className="curto">Qtd
            <input type="number" min="1" value={quantidade} onChange={(e) => setQuantidade(e.target.value)} />
          </label>
          <button className="secundario" onClick={adicionar}>Adicionar</button>
        </div>

        <table>
          <thead><tr><th>Item</th><th className="num">Qtd</th><th className="num">Subtotal</th><th></th></tr></thead>
          <tbody>
            {carrinho.map((it, i) => (
              <tr key={i}>
                <td>{it.produto.descricao}</td>
                <td className="num">{it.quantidade}</td>
                <td className="num">{brl(it.produto.preco * it.quantidade)}</td>
                <td><button className="link" onClick={() => remover(i)}>remover</button></td>
              </tr>
            ))}
            {carrinho.length === 0 && <tr><td colSpan={4} className="muted">Carrinho vazio</td></tr>}
          </tbody>
        </table>

        <div className="form-linha">
          <label className="cresce">Forma de pagamento
            <select value={forma} onChange={(e) => setForma(e.target.value)}>
              {FORMAS.map((f) => <option key={f} value={f}>{f}</option>)}
            </select>
          </label>
          <div className="total">Total <strong>{brl(total)}</strong></div>
        </div>
        <button className="primario" disabled={carrinho.length === 0} onClick={finalizar}>Finalizar venda</button>
      </section>

      <section className="card">
        <h2>Ultima venda</h2>
        {ultima ? (
          <>
            <div className="ok">Venda registrada. Total {brl(ultima.total)}.</div>
            <table>
              <thead><tr><th>Item</th><th className="num">Qtd</th><th className="num">Subtotal</th></tr></thead>
              <tbody>
                {ultima.itens.map((it) => (
                  <tr key={it.produtoId}>
                    <td>{it.descricao}</td>
                    <td className="num">{it.quantidade}</td>
                    <td className="num">{brl(it.subtotal)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            <p className="muted">Forma: {ultima.formaPagamento}</p>
          </>
        ) : (
          <p className="muted">O preco vem do catalogo, e o estoque baixa automaticamente ao finalizar.</p>
        )}
      </section>
    </div>
  );
}
