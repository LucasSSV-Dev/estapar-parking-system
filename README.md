![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.5-6DB33F?logo=springboot)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-Hibernate-59666C)
![JUnit](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5)
![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?logo=mysql)
![Docker](https://img.shields.io/badge/Docker-Container-2496ED?logo=docker)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven)

# Estapar Parking System

> Technical Challenge – Backend Developer

Backend desenvolvido para o desafio técnico da Estapar com o objetivo de simular o gerenciamento de um estacionamento, controlando entrada e saída de veículos, ocupação de vagas e cálculo de receita por setor.

O sistema recebe eventos de um simulador externo via **webhook**, processa regras de negócio relacionadas à ocupação e preços dinâmicos e expõe uma **API REST** para consulta de faturamento.

---

O objetivo do desafio é construir um backend capaz de:

- Gerenciar vagas de estacionamento
- Processar eventos de entrada, estacionamento e saída de veículos
- Controlar ocupação por setor
- Calcular faturamento baseado no tempo de permanência
- Aplicar regras de preço dinâmico conforme a lotação do setor

O sistema recebe eventos de um **simulador de garagem** e deve processá-los corretamente.

Eventos possíveis:

- `ENTRY`
- `PARKED`
- `EXIT`

---

# Tecnologias Utilizadas

- **Java 21**
- **Spring Boot**
- **Spring Data JPA**
- **Hibernate**
- **MySQL**
- **JUnit 5**
- **Mockito**
- **Docker (simulador da garagem)**
- **Maven**
- **SLF4J / Logback**

---

# Arquitetura da Aplicação

O projeto segue uma arquitetura em camadas típica de aplicações Spring Boot.

```

controller
↓
service
↓
repository
↓
database

```

### Controller

Responsável por expor os endpoints REST da aplicação.

Exemplo:

- `/webhook`
- `/revenue`

### Service

Camada onde ficam as **regras de negócio**:

- processamento de eventos do webhook
- controle de vagas ocupadas
- cálculo de preço
- validações

### Repository

Responsável pela comunicação com o banco de dados através do **Spring Data JPA**.

### Domain

Contém:

- entidades do sistema
- enums
- exceções de domínio

---

# Estrutura do Projeto

```

parkingSystem
├── application
│   ├── api
│   │   ├── controller
│   │   └── dto
│   └── service
│
├── domain
│   ├── entity
│   ├── enums
│   └── exception
│
├── infrastructure
│   └── repository

```

Essa separação ajuda a manter:

- responsabilidades claras
- baixo acoplamento
- melhor testabilidade

---

# Fluxo de Eventos

O sistema recebe eventos do simulador e os processa internamente aplicando regras de negócio antes de persistir os dados.

```

Garage Simulator
│
│ POST /webhook
▼
WebhookController
▼
WebhookService
▼
Event Handlers
(ENTRY / PARKED / EXIT)
▼
Business Rules
▼
Database
│
▼
Revenue API
(GET /revenue)

```

---

# Fluxo de Funcionamento

## 1. Inicialização

Ao iniciar a aplicação:

1. O sistema consulta o simulador
2. Busca configuração da garagem via:

```

GET /garage

```

Os dados recebidos incluem:

- setores
- preço base
- capacidade máxima
- vagas disponíveis

Essas informações são persistidas no banco.

---

# Webhook de Eventos

O simulador envia eventos para:

```

POST /webhook

````

## ENTRY

Indica que um veículo entrou na garagem.

```json
{
  "license_plate": "ABC1234",
  "entry_time": "2025-01-01T12:00:00.000Z",
  "event_type": "ENTRY"
}
````

A aplicação:

* valida disponibilidade de vagas
* calcula o preço dinâmico baseado na ocupação
* registra o evento

---

## PARKED

Indica que o veículo estacionou em uma vaga específica.

```json
{
  "license_plate": "ABC1234",
  "lat": -23.561684,
  "lng": -46.655981,
  "event_type": "PARKED"
}
```

A aplicação:

* associa o veículo à vaga
* registra a ocupação do setor

---

## EXIT

Indica que o veículo saiu do estacionamento.

```json
{
  "license_plate": "ABC1234",
  "exit_time": "2025-01-01T12:00:00.000Z",
  "event_type": "EXIT"
}
```

A aplicação:

* libera a vaga
* calcula o valor da estadia
* registra o pagamento

---

# Regras de Negócio

## Ocupação

* Um veículo não pode ocupar mais de uma vaga
* Um setor não pode ultrapassar sua capacidade
* Se o setor atingir **100% de ocupação**, novas entradas são bloqueadas até que uma vaga seja liberada

---

## Cálculo de Preço

Regras implementadas:

1. **Primeiros 30 minutos são gratuitos**

2. Após 30 minutos:

* cobrança por hora
* utilizando `basePrice`
* arredondamento para cima

Exemplo:

```
Tempo estacionado: 1h10
Cobrança: 2 horas
```

---

## Preço Dinâmico

O preço base sofre variação conforme a ocupação do setor no momento da entrada:

| Ocupação   | Regra           |
| ---------- | --------------- |
| < 25%      | desconto de 10% |
| 25% – 50%  | preço normal    |
| 50% – 75%  | aumento de 10%  |
| 75% – 100% | aumento de 25%  |

A regra é aplicada **no momento da entrada do veículo**.

---

# API REST

## Consulta de Receita

Endpoint:

```
GET /revenue
```

Request:

```json
{
  "date": "2025-01-01",
  "sector": "A"
}
```

Response:

```json
{
  "amount": 120.00,
  "currency": "BRL",
  "timestamp": "2025-01-01T12:00:00.000Z"
}
```

Esse endpoint retorna o faturamento total de um setor em uma data específica.

---

# Banco de Dados

O sistema utiliza **MySQL** para persistência dos dados.

Principais entidades:

* `Garage`
* `ParkingSpot`
* `ParkingEvent`

## Subindo o Banco de Dados com Docker

A aplicação utiliza **MySQL** e espera que as credenciais do banco sejam fornecidas
através das variáveis de ambiente `MYSQL_USERNAME` e `MYSQL_PASSWORD`.

Para subir rapidamente um banco de dados local usando **Docker**, execute:

```bash
docker run -d \
  --name estapar_mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=estaparparkingsystem \
  -e MYSQL_USER=estapar \
  -e MYSQL_PASSWORD=estapar \
  mysql:8
  ```

Depois defina as variáveis de ambiente usadas pela aplicação:

export MYSQL_USERNAME=estapar
export MYSQL_PASSWORD=estapar

Em Windows (PowerShell):

$env:MYSQL_USERNAME="estapar"
$env:MYSQL_PASSWORD="estapar"

A aplicação se conectará ao banco utilizando a seguinte configuração:

spring.datasource.url=jdbc:mysql://localhost:3306/estaparparkingsystem

As tabelas são criadas automaticamente pelo Hibernate ao iniciar a aplicação
(spring.jpa.hibernate.ddl-auto=update).

---

# Executando o Projeto

## 1. Clonar o repositório

```bash
git clone https://github.com/LucasSSV-Dev/estapar-parking-system.git
```

---

## 2. Iniciar o simulador da garagem

```bash
docker run -d --name estapar_simulator -p 3000:3000 --add-host=localhost:host-gateway cfontes0estapar/garage-sim:1.0.0
```

---

## 3. Executar a aplicação

```bash
mvn clean install
```

Depois execute:

```
run EstaparParkingSystemApplication
```

A API iniciará em:

```
http://localhost:3003
```

---

# Executando Testes

Para rodar os testes unitários:

```bash
mvn test
```

Os testes utilizam:

* **JUnit 5**
* **Mockito**

---

# Estratégia de Testes

Os testes unitários foram focados principalmente na **camada de serviço**, onde estão concentradas as regras de negócio da aplicação.

A estratégia adotada foi isolar a lógica de negócio utilizando **JUnit 5** e **Mockito**, permitindo validar comportamentos sem dependência direta do banco de dados ou da infraestrutura externa.

Foram testados cenários como:

* processamento de eventos de entrada de veículos
* associação de veículos às vagas
* cálculo de preço baseado no tempo de permanência
* validação de vagas ocupadas
* tratamento de eventos inválidos

---

# Logs

A aplicação utiliza **SLF4J** com **Logback** para registrar:

* processamento de eventos
* erros de validação
* operações importantes do sistema

---

# Tratamento de Erros

A aplicação utiliza exceções de domínio para representar erros relacionados às regras de negócio do estacionamento.

Exemplos de situações tratadas:

* tentativa de entrada quando o estacionamento está cheio
* inconsistência em eventos recebidos pelo webhook
* dados inválidos enviados na requisição

Essas exceções são tratadas por um **ExceptionHandler global**, garantindo respostas HTTP apropriadas e mensagens de erro claras para o consumidor da API.

---

# Melhorias Futuras

Algumas melhorias possíveis:

* documentação com **Swagger/OpenAPI**
* testes de integração
* cache de consultas de receita
* containerização completa da aplicação
* métricas com **Spring Actuator**

---

# Autor

Lucas de Souza Santos Viana

GitHub
[https://github.com/LucasSSV-Dev](https://github.com/LucasSSV-Dev)

---

![License](https://img.shields.io/badge/license-MIT-green)
![GitHub last commit](https://img.shields.io/github/last-commit/LucasSSV-Dev/estapar-parking-system)
![GitHub repo size](https://img.shields.io/github/repo-size/LucasSSV-Dev/estapar-parking-system)
