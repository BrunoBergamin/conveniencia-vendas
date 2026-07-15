import { useEffect, useState, type FormEvent } from "react";
import { catalogo, mensagemErro } from "../api";
import { CATEGORIAS, type Produto } from "../types";
import { brl } from "../util";

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
        <h2>Produtos ({lista.length})</h2>
        {erro && <div className="erro">{erro}</div>}
        <div className="tabela-wrap">
          <table>
            <thead>
              <tr><th>Descricao</th><th>Categoria</th><th className="num">Preco</th><th className="num">Estoque</th></tr>
            </thead>
            <tbody>
              {lista.map((p) => (
                <tr key={p.id}>
                  <td>{p.descricao}</td>
                  <td><span className="chip">{p.categoria}</span></td>
                  <td className="num">{brl(p.preco)}</td>
                  <td className="num">
                    {estoques[p.id] ?? <button className="link" onClick={() => verEstoque(p.id)}>ver</button>}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section className="card">
        <h2>Novo produto</h2>
        <p className="muted">Requer papel GERENTE.</p>
        <form onSubmit={cadastrar} className="form">
          <label>Codigo de barras<input value={codigoBarras} onChange={(e) => setCodigoBarras(e.target.value)} required /></label>
          <label>Descricao<input value={descricao} onChange={(e) => setDescricao(e.target.value)} required /></label>
          <label>Preco<input type="number" step="0.01" min="0" value={preco} onChange={(e) => setPreco(e.target.value)} required /></label>
          <label>Categoria
            <select value={categoria} onChange={(e) => setCategoria(e.target.value)}>
              {CATEGORIAS.map((c) => <option key={c} value={c}>{c}</option>)}
            </select>
          </label>
          <button className="primario">Cadastrar</button>
        </form>
      </section>
    </div>
  );
}
