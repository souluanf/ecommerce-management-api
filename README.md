# Ecommerce Management API

"Elasticsearch configurado sem autenticação para simplicidade do ambiente de desenvolvimento e avaliação"

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=souluanf_fecommerce-management-api&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=souluanf_fecommerce-management-api)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=souluanf_fecommerce-management-api&metric=coverage)](https://sonarcloud.io/summary/new_code?id=souluanf_fecommerce-management-api)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=souluanf_fecommerce-management-api&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=souluanf_fecommerce-management-api)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=souluanf_fecommerce-management-api&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=souluanf_fecommerce-management-api)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=souluanf_fecommerce-management-api&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=souluanf_fecommerce-management-api)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=souluanf_fecommerce-management-api&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=souluanf_fecommerce-management-api)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=souluanf_fecommerce-management-api&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=souluanf_fecommerce-management-api)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=souluanf_fecommerce-management-api&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=souluanf_fecommerce-management-api)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=souluanf_fecommerce-management-api&metric=bugs)](https://sonarcloud.io/summary/new_code?id=souluanf_fecommerce-management-api)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=souluanf_fecommerce-management-api&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=souluanf_fecommerce-management-api)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=souluanf_fecommerce-management-api&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=souluanf_golden-raspberry-awards-api)

## Sumário

- [Funcionalidades](#funcionalidades)
- [Autenticação JWT](#autenticação-jwt)
- [Configuração de Segurança](#configuração-de-segurança)
- [Swagger/OpenAPI](#swaggeropenapi)
- [Requisitos](#requisitos)
- [Configuração](#configuração)
- [Execução](#execução)
    - [Executando a Aplicação com Maven](#executando-a-aplicação-com-maven)
    - [Executando a Aplicação com Docker Compose](#executando-a-aplicação-com-docker-compose)
- [Acesso ao Banco de Dados](#acesso-ao-banco-de-dados)
    - [Credenciais](#credenciais)
- [Contato](#contato)

## Funcionalidades

Esta API oferece um sistema completo de e-commerce com as seguintes funcionalidades:


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

### Executando a Aplicação com Docker Compose

Copie as variáveis utilizadas

```bash
cp example.env .env
```

Execute o comando abaixo:

```bash
docker-compose up -d
```

### Acesso à Documentação

#### Postman (preferência)
Ambas as collections estão no diretório `collections`:
[collections](collections)

Importe ambas as collections para o Postman e teste os serviços

#### OpenAPI

- **OpenApi UI:** [http://localhost:8080/ecommerce-management/swagger-ui/index.html](http://localhost:8080/ecommerce-management/swagger-ui/index.html)

### Credenciais

#### Serviços

| **Serviço** |   **URL**   | **Username** | **Password** | **Database** | **Port** |
|:-----------:|:-----------:|:------------:|:------------:|:------------:|:--------:|
|   `MySQL`   | `localhoat` | `finance_db` | `finance_db` | `finance_db` |  `3306`  |
| `Rabbit MQ` | `localhoat` |  `rabbitmq`  |  `rabbitmq`  |  `rabbitmq`  | `15672`  |

#### Auth

| **Username** | **Password** |
|:------------:|:------------:|
|   `admin`    |   `admin`    |

## Desenho da solução

![finance-management-java.png](ecommerce-management-api.png)

## Contato

Para suporte ou feedback:

- **Nome:** Luan Fernandes
- **Email:**  [contact@luanfernandes.dev](mailto:contact@luanfernandes.dev)
- **Website:** [https://luanfernandes.dev](https://luanfernandes.dev)
- **LinkedIn:** [https://linkedin.com/in/souluanf](https://linkedin.com/in/souluanf)