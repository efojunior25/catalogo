# Frontend - Catalogo de Produtos

## Descrição

Interface web desenvolvida em React com TypeScript para navegação de produtos, gerenciamento de carrinho e finalização de pedidos.

## Tecnologias Utilizadas

* React 19.1.1
* TypeScript 4.9.5
* CSS3 (sem frameworks externos)
* Create React App
* Testing Library

## Funcionalidades

* Listagem de produtos com paginação
* Busca por nome com debounce (300ms)
* Carrinho lateral com controles de quantidade
* Checkout com tratamento de erros de estoque
* Interface responsiva
* Acessibilidade (aria-labels)
* Feedback visual para ações do usuário

## Como Executar

### Pré-Requisitos

* node.js ou superior
* npm ou yarn

### Execução

### `npm install`
### `npm start`

## Funcionalidades Detalhadas

### 1 - Listagem de Produtos

* Paginação: 6 produtos por página
* Busca: Filtro por nome com debounce de 300ms
* Estoque: Indicação visual de produtos em estoque/fora de estoque 
* Responsiva: Grid adaptativo para diferentes tamanhos de tela

### 2 - Carrinho de Compras

* Lateral: Abre/fecha com animação suave
* Controles: Botões +/- para ajustar quantidade
* Remoção: Botão para remover item completamente
* Total: Cálculo automático do valor total
* Contador: Badge no botão do carrinho

### 3 - Checkout

* Validação: Verifica estoque antes de finalizar
* Feedback: Mensagens de sucesso/erro claras
* Rollback: Interface atualiza automaticamente em caso de erro
* ID do Pedido: Exibe ID após sucesso

### 4 - Acessibilidade

* ARIA Labels: Todos os botões e inputs têm labels descritivos
* Navegação: Suporte completo a teclado
* Contraste: Cores com contraste adequado
* Foco: Indicadores visuais de foco