# 💰 CashHub — Intelligent Personal Finance Manager

O **CashHub** é uma aplicação web de gerenciamento financeiro pessoal desenvolvida para facilitar o controle de receitas, despesas e organização financeira.

O sistema permite registrar e acompanhar movimentações financeiras, visualizar indicadores em um dashboard interativo e obter análises financeiras automatizadas utilizando Inteligência Artificial executada localmente através do Ollama.

O projeto foi desenvolvido utilizando Java, Spring Boot, PostgreSQL, TypeScript e tecnologias web, com foco em simplicidade, organização e integração entre diferentes tecnologias.

## ✨ Funcionalidades

- **Dashboard financeiro:** visualização de receitas, despesas e saldo do período selecionado.
- **Gerenciamento de movimentações:** cadastro, edição, listagem e exclusão de receitas e despesas.
- **Categorização:** organização das movimentações por categorias.
- **Filtro por período:** consulta dos dados financeiros por mês.
- **Gráficos interativos:** visualização da distribuição de despesas por categoria utilizando Chart.js.
- **CashHub AI:** análise automatizada da situação financeira, identificação das principais categorias de gastos e sugestões para melhorar a organização financeira.

## 🤖 Inteligência Artificial

Um dos principais diferenciais do CashHub é a integração com Inteligência Artificial utilizando o **Ollama**.

Diferentemente de aplicações que dependem de APIs comerciais, o CashHub utiliza um modelo de linguagem executado localmente na máquina do usuário.

O modelo utilizado é o **Llama 3.2 3B**.

### Como funciona a integração?

1. O usuário seleciona o período financeiro que deseja analisar.
2. O frontend envia uma solicitação para a API desenvolvida com Spring Boot.
3. O backend consulta as movimentações e os resumos financeiros armazenados no PostgreSQL.
4. Os dados são organizados e enviados ao modelo de IA através da API local do Ollama.
5. O modelo processa as informações e gera uma análise financeira.
6. O backend retorna a resposta ao frontend, que apresenta o resultado na interface do CashHub.

A análise apresenta:

- Resumo da situação financeira.
- Avaliação das receitas e despesas.
- Identificação das categorias que mais consumiram dinheiro.
- Sugestões práticas para melhorar a organização financeira.

**Privacidade:** o modelo é executado localmente através do Ollama. Não é necessária uma chave de API paga de inteligência artificial. A integração utiliza o serviço local do Ollama.

## 🛠️ Tecnologias utilizadas

### Backend

- Java 25
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA
- Hibernate
- Maven
- PostgreSQL

### Frontend

- HTML5
- CSS3
- TypeScript 5.8.3
- Chart.js
- Node.js
- npm
- http-server

### Inteligência Artificial

- Ollama
- Llama 3.2 3B

## 📋 Requisitos para instalação

Antes de executar o projeto, instale as seguintes ferramentas:

| Ferramenta | Versão utilizada |
|---|---|
| Java JDK | 25 |
| PostgreSQL | 18 ou compatível |
| Node.js | 22 ou superior |
| npm | 10 ou superior |
| Ollama | Versão atual |
| Git | Versão atual |

Também é recomendado utilizar o Visual Studio Code ou outra IDE de sua preferência.

**Importante:** o Maven Wrapper já acompanha o backend. Portanto, não é necessário instalar o Maven separadamente.

## 🚀 Como executar o projeto

### 1. Clonar o repositório

Abra o terminal e execute:

```bash
git clone https://github.com/SEU-USUARIO/CashHub.git
```

Entre na pasta do projeto:

```bash
cd CashHub
```

### 2. Configurar o PostgreSQL

Instale e inicie o PostgreSQL.

Crie o banco de dados utilizado pelo projeto:

```sql
CREATE DATABASE cashhub;
```

O backend utiliza variáveis de ambiente para acessar o banco de dados. Isso evita armazenar senhas diretamente nos arquivos do projeto.

O arquivo `backend/cashhub/src/main/resources/application.properties` já possui a seguinte configuração:

```properties
spring.application.name=cashhub

spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/cashhub}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

Configure as seguintes variáveis de ambiente na sua máquina:

| Variável | Finalidade | Valor padrão ou exemplo |
|---|---|---|
| `DB_URL` | Endereço do banco | `jdbc:postgresql://localhost:5432/cashhub` |
| `DB_USERNAME` | Usuário do PostgreSQL | `postgres` |
| `DB_PASSWORD` | Senha do PostgreSQL | Sua senha local |

**Windows (PowerShell):**

```powershell
[Environment]::SetEnvironmentVariable("DB_URL", "jdbc:postgresql://localhost:5432/cashhub", "User")
[Environment]::SetEnvironmentVariable("DB_USERNAME", "postgres", "User")
```

Para configurar a senha sem deixá-la escrita no comando ou no histórico do terminal:

```powershell
$secure = Read-Host "Digite a senha do PostgreSQL" -AsSecureString
$ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)

try {
    $plain = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)
    [Environment]::SetEnvironmentVariable("DB_PASSWORD", $plain, "User")
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
    Remove-Variable plain, secure, ptr -ErrorAction SilentlyContinue
}
```

Após configurar as variáveis, abra um novo terminal para que o Windows carregue os valores atualizados.

**Linux ou macOS:**

Configure as variáveis no ambiente do terminal:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/cashhub"
export DB_USERNAME="postgres"
export DB_PASSWORD="SUA_SENHA_LOCAL"
```

Substitua o valor de exemplo pela senha do seu próprio PostgreSQL. Evite salvar credenciais diretamente nos arquivos versionados do projeto.

### 3. Executar o backend

Abra um terminal na pasta:

```bash
cd backend/cashhub
```

Execute a aplicação:

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux ou macOS:**

```bash
./mvnw spring-boot:run
```

O Spring Boot iniciará o backend na porta:

```text
http://localhost:8080
```

Mantenha o terminal aberto enquanto utilizar a aplicação.

### 4. Executar o frontend

Abra outro terminal na pasta:

```bash
cd frontend
```

Instale as dependências:

```bash
npm install
```

Compile o TypeScript:

```bash
npm run build
```

Inicie o servidor frontend:

```bash
npm run serve
```

A aplicação ficará disponível em:

```text
http://localhost:5500
```

**Observação:** a compilação gera automaticamente a pasta `dist/`, que contém o JavaScript utilizado pela interface. Essa pasta não precisa ser enviada ao GitHub, pois pode ser recriada com `npm run build`.

### 5. Configurar a Inteligência Artificial

Instale o Ollama através do site oficial:

https://ollama.com/download

Depois da instalação, abra o terminal e execute:

```bash
ollama pull llama3.2:3b
```

Esse comando baixa o modelo de linguagem utilizado pelo CashHub.

Certifique-se de que o Ollama esteja em execução.

A API local do Ollama utiliza o endereço:

```text
http://localhost:11434
```

O CashHub utiliza esse serviço para processar as solicitações de análise financeira.

**Observação:** a aplicação principal funciona sem a IA, mas a funcionalidade CashHub AI depende do Ollama e do modelo instalado.

## 📂 Estrutura do projeto

```text
CashHub/
│
├── backend/
│   └── cashhub/
│       ├── .mvn/
│       │   └── wrapper/
│       │       └── maven-wrapper.properties
│       │
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/br/com/cashhub/
│       │   │   │   ├── config/
│       │   │   │   ├── controller/
│       │   │   │   ├── dto/
│       │   │   │   ├── exception/
│       │   │   │   ├── model/
│       │   │   │   ├── repository/
│       │   │   │   └── service/
│       │   │   │
│       │   │   └── resources/
│       │   │       └── application.properties
│       │   │
│       │   └── test/
│       │       └── java/br/com/cashhub/
│       │
│       ├── mvnw
│       ├── mvnw.cmd
│       ├── pom.xml
│       └── .gitignore
│
├── frontend/
│   ├── css/
│   │   └── styles.css
│   │
│   ├── src/
│   │   └── app.ts
│   │
│   ├── index.html
│   ├── package.json
│   ├── package-lock.json
│   └── tsconfig.json
│
├── .gitignore
└── README.md
```

## 🔌 Comunicação entre os serviços

O CashHub utiliza três serviços principais:

| Serviço | Endereço | Responsabilidade |
|---|---|---|
| Frontend | `localhost:5500` | Interface do usuário |
| Backend | `localhost:8080` | API e processamento de dados |
| Ollama | `localhost:11434` | Processamento da Inteligência Artificial |

O PostgreSQL é utilizado pelo backend para armazenar permanentemente as movimentações financeiras.

## 🎯 Objetivo do projeto

O CashHub foi desenvolvido como projeto de portfólio com o objetivo de aplicar conhecimentos de desenvolvimento de software, integração entre frontend e backend, persistência de dados, criação de APIs REST e utilização de modelos de Inteligência Artificial executados localmente.

O projeto demonstra a integração de diferentes tecnologias em uma aplicação funcional, com foco em uma experiência simples e intuitiva para o gerenciamento financeiro pessoal.

---

**Desenvolvido por F. Santos**