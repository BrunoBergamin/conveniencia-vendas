# Deploy no Kubernetes

Manifestos em kustomize: namespace, ConfigMap, Secret, dois PostgreSQL (um por
serviço), os dois serviços com probes de liveness/readiness, HPA e anotações de
scrape do Prometheus, e um Ingress por host.

## 1. Construir as imagens

```bash
docker build -t conveniencia/catalogo-service:1.0.0 ./catalogo-service
docker build -t conveniencia/vendas-service:1.0.0 ./vendas-service

# O frontend embute as URLs das APIs no build. Aponte para os hosts do ingress:
docker build -t conveniencia/frontend:1.0.0 \
  --build-arg VITE_CATALOGO_URL=http://catalogo.conveniencia.local \
  --build-arg VITE_VENDAS_URL=http://vendas.conveniencia.local \
  ./frontend
```

Em cluster local (kind/minikube), carregue as imagens no cluster (senão o pod não
acha a imagem, pois não há registry):

```bash
# kind
kind load docker-image conveniencia/catalogo-service:1.0.0
kind load docker-image conveniencia/vendas-service:1.0.0
kind load docker-image conveniencia/frontend:1.0.0
# minikube
minikube image load conveniencia/catalogo-service:1.0.0
minikube image load conveniencia/vendas-service:1.0.0
minikube image load conveniencia/frontend:1.0.0
```

## 2. Aplicar

```bash
kubectl apply -k deploy/k8s
kubectl -n conveniencia get pods,svc,hpa
```

## 3. Acessar

Com Ingress (ative o addon no minikube: `minikube addons enable ingress`), aponte
os hosts no seu `/etc/hosts`:

```
127.0.0.1 app.conveniencia.local catalogo.conveniencia.local vendas.conveniencia.local
```

O frontend fica em `http://app.conveniencia.local`.

Ou, sem Ingress, use port-forward:

```bash
kubectl -n conveniencia port-forward svc/catalogo-service 8081:8081
kubectl -n conveniencia port-forward svc/vendas-service 8082:8082
```

## Observacoes

- **Segredos:** `secret.yaml` traz valores de exemplo. Em producao use Sealed
  Secrets, External Secrets ou Vault, e nunca versione segredos reais.
- **HPA:** precisa do metrics-server no cluster (`minikube addons enable metrics-server`).
- **Bancos:** cada serviço tem o seu PostgreSQL com PVC (padrao *database per service*).
