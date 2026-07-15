// Icones em SVG inline (estilo feather, stroke = currentColor).
type P = { size?: number };
const base = (size = 18) => ({
  width: size,
  height: size,
  viewBox: "0 0 24 24",
  fill: "none",
  stroke: "currentColor",
  strokeWidth: 1.8,
  strokeLinecap: "round" as const,
  strokeLinejoin: "round" as const,
});

export const IconBox = ({ size }: P) => (
  <svg {...base(size)}><path d="M21 8 12 3 3 8v8l9 5 9-5V8Z" /><path d="m3 8 9 5 9-5" /><path d="M12 21V13" /></svg>
);
export const IconWallet = ({ size }: P) => (
  <svg {...base(size)}><path d="M3 7a2 2 0 0 1 2-2h12v4" /><path d="M3 7v10a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-6a2 2 0 0 0-2-2H5a2 2 0 0 1-2-2Z" /><circle cx="16.5" cy="13" r="1.2" fill="currentColor" stroke="none" /></svg>
);
export const IconCart = ({ size }: P) => (
  <svg {...base(size)}><circle cx="9" cy="20" r="1.4" /><circle cx="18" cy="20" r="1.4" /><path d="M2 3h2l2.4 12.3a1.6 1.6 0 0 0 1.6 1.3h8.7a1.6 1.6 0 0 0 1.6-1.3L21 7H5.2" /></svg>
);
export const IconChart = ({ size }: P) => (
  <svg {...base(size)}><path d="M3 3v18h18" /><rect x="7" y="11" width="3" height="6" rx="0.6" /><rect x="12" y="7" width="3" height="10" rx="0.6" /><rect x="17" y="13" width="3" height="4" rx="0.6" /></svg>
);
export const IconLogout = ({ size }: P) => (
  <svg {...base(size)}><path d="M15 4h3a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2h-3" /><path d="M10 17 5 12l5-5" /><path d="M5 12h11" /></svg>
);
export const IconPlus = ({ size }: P) => (
  <svg {...base(size)}><path d="M12 5v14M5 12h14" /></svg>
);
export const IconCheck = ({ size }: P) => (
  <svg {...base(size)}><path d="M20 6 9 17l-5-5" /></svg>
);
export const IconStore = ({ size }: P) => (
  <svg {...base(size)}><path d="M3 9 4.5 4h15L21 9" /><path d="M4 9v10a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1V9" /><path d="M3 9a2.5 2.5 0 0 0 5 0 2.5 2.5 0 0 0 5 0 2.5 2.5 0 0 0 5 0 2.5 2.5 0 0 0 3 0" /><path d="M9 20v-5h6v5" /></svg>
);
export const IconTrash = ({ size }: P) => (
  <svg {...base(size)}><path d="M4 7h16M9 7V5a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2m2 0v12a1 1 0 0 1-1 1H8a1 1 0 0 1-1-1V7" /></svg>
);
