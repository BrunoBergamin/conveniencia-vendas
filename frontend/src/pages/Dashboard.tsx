import { useState } from "react";
import Produtos from "../components/Produtos";
import Caixa from "../components/Caixa";
import Venda from "../components/Venda";
import Relatorio from "../components/Relatorio";

type Aba = "produtos" | "caixa" | "venda" | "relatorio";

const ABAS: { id: Aba; label: string }[] = [
  { id: "produtos", label: "Produtos" },
  { id: "caixa", label: "Caixa" },
  { id: "venda", label: "Nova venda" },
  { id: "relatorio", label: "Relatorio" },
];

export default function Dashboard({ onSair }: { onSair: () => void }) {
  const [aba, setAba] = useState<Aba>("produtos");

  return (
    <div className="app">
      <header className="topbar">
        <div className="marca"><i />Conveniencia <span>PDV</span></div>
        <nav>
          {ABAS.map((a) => (
            <button key={a.id} className={aba === a.id ? "ativa" : ""} onClick={() => setAba(a.id)}>
              {a.label}
            </button>
          ))}
        </nav>
        <button className="sair" onClick={onSair}>Sair</button>
      </header>
      <main className="conteudo">
        {aba === "produtos" && <Produtos />}
        {aba === "caixa" && <Caixa />}
        {aba === "venda" && <Venda />}
        {aba === "relatorio" && <Relatorio />}
      </main>
    </div>
  );
}
