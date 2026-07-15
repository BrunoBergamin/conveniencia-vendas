import { useState, type ReactNode } from "react";
import Produtos from "../components/Produtos";
import Caixa from "../components/Caixa";
import Venda from "../components/Venda";
import Relatorio from "../components/Relatorio";
import { getUsuario } from "../api";
import { IconBox, IconWallet, IconCart, IconChart, IconLogout, IconStore } from "../icons";

type Aba = "produtos" | "caixa" | "venda" | "relatorio";

const ABAS: {
  id: Aba;
  label: string;
  titulo: string;
  sub: string;
  icone: ReactNode;
}[] = [
  { id: "produtos", label: "Produtos", titulo: "Produtos", sub: "Catalogo e estoque da loja", icone: <IconBox /> },
  { id: "caixa", label: "Caixa", titulo: "Caixa", sub: "Abertura e fechamento do turno", icone: <IconWallet /> },
  { id: "venda", label: "Nova venda", titulo: "Nova venda", sub: "Registrar venda no caixa aberto", icone: <IconCart /> },
  { id: "relatorio", label: "Relatorio", titulo: "Relatorio do dia", sub: "Faturamento e itens mais vendidos", icone: <IconChart /> },
];

export default function Dashboard({ onSair }: { onSair: () => void }) {
  const [aba, setAba] = useState<Aba>("produtos");
  const usuario = getUsuario();
  const atual = ABAS.find((a) => a.id === aba)!;
  const iniciais = (usuario?.login ?? "??").slice(0, 2).toUpperCase();

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark"><IconStore size={19} /></span>
          Conveniencia
          <span className="brand-tag">PDV</span>
        </div>
        <nav className="nav">
          <span className="nav-label">Operacao</span>
          {ABAS.map((a) => (
            <button
              key={a.id}
              className={"nav-item" + (aba === a.id ? " ativo" : "")}
              onClick={() => setAba(a.id)}
            >
              {a.icone}
              <span>{a.label}</span>
            </button>
          ))}
        </nav>
      </aside>

      <div className="main">
        <header className="topbar">
          <div>
            <h1 className="page-title">{atual.titulo}</h1>
            <div className="page-sub">{atual.sub}</div>
          </div>
          <div className="topbar-right">
            <div className="userchip">
              <span className="avatar">{iniciais}</span>
              <span className="userchip-info">
                <strong>{usuario?.login ?? "usuario"}</strong>
                <span>{usuario?.papel ?? ""}</span>
              </span>
            </div>
            <button className="icon-btn" onClick={onSair} title="Sair" aria-label="Sair">
              <IconLogout size={17} />
            </button>
          </div>
        </header>

        <main className="page">
          {aba === "produtos" && <Produtos />}
          {aba === "caixa" && <Caixa />}
          {aba === "venda" && <Venda />}
          {aba === "relatorio" && <Relatorio />}
        </main>
      </div>
    </div>
  );
}
