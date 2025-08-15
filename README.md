# Catálogo de Produtos

## Descrição do Projeto

Aplicação fullstack para catálogo de produtos com funcionalidades de carrinho de compras e checkout Atômico. Desenvolvida seguindo especificações rigorosas de arquitetura e boas práticas.

## Arquitetura

* Backend: Spring Boot (Java 21) + H2 Database
* Frontend: React(TypeScript) + CSS3
* Padrão: API REST + SPA

## Funcionalidades Implementadas

### Backend

* API REST com endpoint obrigatórios
* Listagem paginada de produtos com busca
* Checkout Atômico com controle de concorrência
* Validação de dados com Bean Validation
* Tratamento de erros 409 para estoque insuficiente
* Transações com rollback automático
* Testes unitários para regras de estoque
* Query otimizada para top 3 produtos vendidos

### Frontend

* Interface responsiva e acessível
* Busca com debounce de 300ms
* Paginaçào dimples e intuitiva
* Carrinho lateral com controles +/-
* Feedback visual para todas as ações
* Tratamente de erros de estoque (409)
* Aria labels para acessibilidade

## Executar o Projeto via Script

### Pré-requisitos
* Java 17+ - Backend
* Node.js 16+ - Frontend
* Git - Clone Repo
* IDE Recomendada: IntelliJ IDEA

NOTA: Projeto desenvolvido e testado no Windows 11 com IntelliJ IDEA

### 1 - Clonar Repositorio

### `git clone https://github.com/efojunior25/catalogo`
### cd catalogo

### 2 - Executar Projeto

O projeto possui um script (`scripts/dev.ps1`) que inicia tanto o backend quanto o frontend para desenvolvimento

### Windows

Abra o PowerShell na raiz do projeto e execute o comando
### `powershell -ExecutionPolicy Bypass -File ./scripts/dev.ps1`


### Linux / macOS

Caso tenha o PowerShell Core instalado execute o comando na raiz do projeto

### `pwsh -ExecutionPolicy Bypass -File ./scripts/dev.ps1`

Caso for utilizar o bash utilize o script `./scripts/dev.sh` e o comando

### `chmod +x scripts/dev.sh`
### `./scripts/dev.sh`


## URLs de acesso

### `Frontend - http://localhost:3000`
### `Backend API - http://localhost:8080/api/v1`
### `H2 Console - http://localhost:8080/api/v1/h2-console`

### Credenciais H2 Console
* URL: jdbc:h2:mem:catalogodb
* Usuario: sa
* Senha: (vazio)