export type Produto = {
  id: string;
  codigoBarras: string;
  descricao: string;
  preco: number;
  categoria: string;
  ativo: boolean;
};

export type Estoque = { produtoId: string; quantidade: number };

export type Caixa = {
  id: string;
  operador: string;
  status: "ABERTO" | "FECHADO";
  fundoTroco: number;
  aberturaEm: string;
  fechamentoEm: string | null;
};

export type ItemVenda = {
  produtoId: string;
  descricao: string;
  precoUnitario: number;
  quantidade: number;
  subtotal: number;
};

export type Venda = {
  id: string;
  caixaId: string;
  itens: ItemVenda[];
  formaPagamento: string;
  total: number;
  criadaEm: string;
};

export type Resumo = {
  quantidadeVendas: number;
  total: number;
  porFormaPagamento: Record<string, number>;
  topProdutos: { produtoId: string; descricao: string; quantidade: number; total: number }[];
};

export const CATEGORIAS = ["BEBIDA", "ALIMENTO", "LIMPEZA", "HIGIENE", "TABACARIA", "OUTROS"];
export const FORMAS = ["DINHEIRO", "CARTAO_CREDITO", "CARTAO_DEBITO", "PIX"];
