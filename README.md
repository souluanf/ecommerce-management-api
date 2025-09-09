# Ecommerce Management API

Sistema de gerenciamento de pedidos e produtos para e-commerce desenvolvido com Spring Boot, seguindo princípios de Clean Architecture e padrões de desenvolvimento empresarial.

## Sumário

- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Padrões e Arquitetura](#padrões-e-arquitetura)
- [Swagger](#swagger)
- [Requisitos](#requisitos)
- [Configuração](#configuração)
- [Execução](#execução)
- [Contato](#contato)

## Funcionalidades

### Autenticação e Autorização
- Autenticação JWT com Spring Security
- Perfis de usuário (Admin/User)
- Controle de acesso baseado em roles

### Gestão de Produtos
- CRUD completo de produtos
- Busca avançada com Elasticsearch
- Indexação automática
- Controle de estoque com tratamento de race conditions
- Filtros por categoria, nome e faixa de preço
- Busca com tolerância a erros de digitação

### Gestão de Pedidos
- Criação de pedidos com múltiplos produtos
- Validação de estoque em tempo real
- Status automático (PENDENTE/PAGO/CANCELADO)
- Processamento de pagamentos
- Atualização assíncrona de estoque via eventos

### Relatórios Analíticos
- Top 5 usuários que mais compraram
- Ticket médio por usuário
- Faturamento total do mês atual
- Filtros por período de datas

### Busca e Pesquisa
- 8 endpoints de busca especializados
- Busca fuzzy com tolerância a erros
- Autocomplete e sugestões
- Paginação e ordenação
- Apenas produtos disponíveis em estoque

### Mensageria
- Eventos assíncronos com Kafka
- Consumer para atualização de estoque
- Dead Letter Queue para tratamento de falhas
- Idempotência implementada


## Tecnologias

### Core
- Java 21
- Spring Boot 3.5.5
- Spring Security
- Spring Data JPA
- Maven 3.6+

### Banco de Dados
- MySQL 8.0
- Flyway (migrations)

### Busca
- Elasticsearch 8.0
- Spring Data Elasticsearch

### Mensageria
- Apache Kafka
- Spring Kafka

### Documentação
- OpenAPI 3.0 (Swagger)
- Swagger UI

### Testes
- JUnit 5
- TestContainers
- Spring Boot Test

### DevOps
- Docker
- Docker Compose

## Padrões e Arquitetura

### Arquiteturas
- Clean Architecture (Hexagonal)
- Ports and Adapters
- Domain-Driven Design (DDD)

### Padrões de Design
- Use Case Pattern
- Repository Pattern
- Value Object Pattern
- Command Query Responsibility Segregation (CQRS)
- Event-Driven Architecture

### Padrões de Desenvolvimento
- SOLID Principles
- Dependency Injection
- Inversion of Control
- Transaction Management
- Exception Handling

### Padrões de Integração
- Event Sourcing
- Saga Pattern
- Circuit Breaker Pattern
- Retry Pattern com Exponential Backoff

## Swagger

- **OpenAPI UI:** [http://localhost:8080/ecommerce-management/swagger-ui/index.html](http://localhost:8080/ecommerce-management/swagger-ui/index.html)
- **API Docs:** [http://localhost:8080/ecommerce-management/v3/api-docs](http://localhost:8080/ecommerce-management/v3/api-docs)

## Requisitos

- JDK 21
- Maven 3.6+
- Docker

## Configuração

**Instalação do JDK, Maven e Docker:**

- [Instruções para instalação do JDK](https://docs.oracle.com/en/java/javase/21/install/overview-jdk-installation.html)
- [Instruções para instalação do Maven](https://maven.apache.org/install.html)
- [Instruções para instalação do Docker](https://docs.docker.com/get-docker/)

## Execução

Copie as variáveis utilizadas

```bash
cp example.env .env
```

Execute o comando abaixo:

```bash
docker-compose up -d
```

### Autenticação

Após a inicialização da aplicação, é necessário criar um perfil de administrador e obter o token de autenticação para fazer as requisições aos endpoints protegidos.

1. **Criar usuário admin:** Use o endpoint `/auth/register` para criar um usuário com perfil ADMIN (já retorna o token)
2. **Obter token:** Use o endpoint `/auth/login` para obter o token JWT
3. **Usar token:** Adicione o token no header `Authorization: Bearer {token}` em todas as requisições

## Contato

Para suporte ou feedback:

- **Nome:** Luan Fernandes
- **Email:**  [contact@luanfernandes.dev](mailto:contact@luanfernandes.dev)
- **Website:** [https://luanfernandes.dev](https://luanfernandes.dev)
- **LinkedIn:** [https://linkedin.com/in/souluanf](https://linkedin.com/in/souluanf)