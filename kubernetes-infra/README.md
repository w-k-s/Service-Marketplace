# Kubernetes Setup

## 1. Minikube

**Prerequisites**

1. Helm

**Steps**

1. Install Helm dependencies

```sh
helm install rabbitmq -f local/helm/rabbitmq-values.yml bitnami/rabbitmq
```

2. Install global workloads/listeners

```sh
kubectl apply -f .
```

3. Install minikube workloads

```sh
kubectl apply -f local/*.yml
```

**TODO:** `local/workloads` does not create keycloak database on initialization. Needs to be fixed.
