# ---- build ----
FROM node:20-alpine AS build
WORKDIR /app
# URLs das APIs sao embutidas no bundle em tempo de build (Vite). Ajuste por
# ambiente: em compose, os defaults (localhost) servem; em K8s, aponte para o
# ingress via --build-arg.
ARG VITE_CATALOGO_URL=http://localhost:8081
ARG VITE_VENDAS_URL=http://localhost:8082
ENV VITE_CATALOGO_URL=$VITE_CATALOGO_URL
ENV VITE_VENDAS_URL=$VITE_VENDAS_URL
COPY package.json package-lock.json* ./
RUN npm ci || npm install
COPY . .
RUN npm run build

# ---- runtime (nginx) ----
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
