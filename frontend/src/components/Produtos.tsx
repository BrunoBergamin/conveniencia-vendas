import { useEffect, useState, type FormEvent } from "react";
import { catalogo, mensagemErro } from "../api";
import { CATEGORIAS, type Produto } from "../types";
import { brl } from "../util";
import { IconPlus } from "../icons";

export default function Produtos() {
  const [lista, setLista] = useState<Produto[]>([]);
  const [estoques, setEstoques] = useState<Record<string, number>>({});
  const [erro, setErro] = useState<string | null>(null);

  const [codigoBarras, setCodigoBarras] = useState("");
  const [descricao, setDescricao] = useState("");
  const [preco, setPreco] = useState("");
  const [categoria, setCategoria] = useState(CATEGORIAS[0]);

  async function carregar() {
    try {
      const { data } = await catalogo.get<Produto[]>("/produtos");
      setLista(data);
    } catch (e) {
      setErro(mensagemErro(e));
    }
  }

  useEffect(() => {
    carregar();
  }, []);

  async function cadastrar(e: FormEvent) {
    e.preventDefault();
    setErro(null);
    try {
      await catalogo.post("/produtos", {
        codigoBarras,
        descricao,
        preco: Number(preco),
        categoria,
      });
      setCodigoBarras("");
      setDescricao("");
      setPreco("");
      await carregar();
    } catch (err) {
      setErro(mensagemErro(err));
    }
  }

  async function verEstoque(id: string) {
    try {
      const { data } = await catalogo.get<{ quantidade: number }>(`/estoque/${id}`);
      setEstoques((s) => ({ ...s, [id]: data.quantidade }));
    } catch (e) {
      setErro(mensagemErro(e));
    }
  }

  return (
    <div className="grid-2">
      <section className="card">
        <div className="card-head">
          <div>
            <h2>Catalogo</h2>
            <div className="sub">Preco e estoque atual de cada item</div>
          </div>
          <span className="chip">{lista.length} itens</span>
        </div>
        {erro && <div className="card-pad" style={{ paddingBottom: 0 }}><div className="alert alert-erro">{erro}</div></div>}
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Descricao</th>
                <th>Categoria</th>
                <th className="right">Preco</th>
                <th className="right">Estoque</th>
              </tr>
            </thead>
            <tbody>
              {lista.map((p) => (
                <tr key={p.id}>
                  <td className="cell-strong">{p.descricao}</td>
                  <td><span className="chip">{p.categoria}</span></td>
                  <td className="right">{brl(p.preco)}</td>
                  <td className="right">
                    {estoques[p.id] !== undefined ? (
                      estoques[p.id]
                    ) : (
                      <button className="btn btn-ghost btn-sm" onClick={() => verEstoque(p.id)}>ver</button>
                    )}
                  </td>
                </tr>
              ))}
              {lista.length === 0 && (
                <tr><td colSpan={4} className="empty">Nenhum produto cadastrado ainda.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </section>

      <section className="card">
        <div className="card-head">
          <div>
            <h2>Novo produto</h2>
            <div className="sub">Requer papel GERENTE</div>
          </div>
        </div>
        <form onSubmit={cadastrar} className="card-pad">
          <label className="field">
            <span>Codigo de barras</span>
            <input value={codigoBarras} onChange={(e) => setCodigoBarras(e.target.value)} placeholder="7891000000000" required />
          </label>
          <label className="field">
            <span>Descricao</span>
            <input value={descricao} onChange={(e) => setDescricao(e.target.value)} placeholder="Refrigerante lata 350ml" required />
          </label>
          <div className="field-row">
            <label className="field">
              <span>Preco</span>
              <input type="number" step="0.01" min="0" value={preco} onChange={(e) => setPreco(e.target.value)} placeholder="0,00" required />
            </label>
            <label className="field">
              <span>Categoria</span>
              <select value={categoria} onChange={(e) => setCategoria(e.target.value)}>
                {CATEGORIAS.map((c) => <option key={c} value={c}>{c}</option>)}
              </select>
            </label>
          </div>
          <button className="btn btn-primary btn-block" style={{ marginTop: 4 }}>
            <IconPlus size={16} /> Cadastrar produto
          </button>
        </form>
      </section>
    </div>
  );
}
