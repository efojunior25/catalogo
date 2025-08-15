# Backend - Catálogo de Produtos

## Descrição

API REST desenvolvida em Java com Spring Boot para gerenciamento de catálogo de produtos, carrinho de compras e checkout atômico.

## Tecnologias Utilizadas
* Java 17
* Spring Boot 3.5.4
* Maven
* Spring Data JPA
* Spring Valitation
* H2 Database - Em memória
* Lombok

## Como Executar

### Pré-Requisitos
* java 17 ou superior
* Maven ou wrapper incluido

### Passos para execução

OBS: A aplicação possui um passo a passo geral que executa toda a aplicação explicada no Read Me do projeto Geral

### Execução com Maven

### `mvn clean spring-boot:run`

## Endpoint da API

### Produtos
### GET `/api/v1/products?search=&page=&size=`
  * Retorna lista paginada e filtrada por nome, com parametros opcionais de search(filtra por nome), page(pagina) e size(tamanho da pagina)

### Pedidos
### Post `/api/v1/orders`
  * Cria um novo pedido
    * Body
    {
      "items": [
        { "productId": 1, "quantity": 2},
        { "productId": 3, "quantity": 1}
      ]
    }
    * Respostas:
      * 201: Pedido criado com sucesso
      * 409: Erro de estoque insuficiente
      * 400: Dados inválidos

## Banco de Dados

### Configuração H2
* URL: jdbc:h2:mem:catalogodb
* Console: http://localhost:8080/api/v1/h2-console
* Usuario: sa
* Senha: (vazio)

### Query Top 3 Produtos mais Vendidos

SQL QUERY
SELECT p.id, p.name, p.price, p.stock, p.active, p.version,
COALESCE(SUM(oi.quantity), 0) as total_sold
FROM products p
LEFT JOIN order_items oi ON p.id = ou.product.id
WHERE p.active = true
GROUP BY p.id, p.name, p.price, p.stock, p.active, p.version
ORDER BY total_sold DESC
LIMIT 3;

QUERY PLAN: 
Limit (cost=X..Y rows=3 width=Z)
    -> Sort (cost=X...Y rows=N width=Z)
            Sort Key: (COALESCE(sum(oi.quantity), 0)) DESC
            -> HashAggregate (cost=X..Y rows=N width=z)
                Gruop Key: p.id, p.name, p.price, p.stock, p.active, p.version
                -> Hash Left Join (Cost=X..Y rows=N width=Z)
                    Hash Cond: (p.id = oi.product_id)
                    -> Seq Scan on products p (cost=X...Y rows=N width=Z)
                        Filter: (active = true)
                    -> Hash (cost=X..Y rows=N width=Z)
                        -> Seq Scan on order_items oi (cost=X..Y rows=N width=Z)

## Atomicidade e Rollback

### Controle de Concorrência
* @Version implementado na entidade Product para controle de concorrência otimista
  * Previne condições de corrida em atualizações simultâneas de estoque

### Transação Atômica
* @Transactional aplicado no metodo createOrder() do OrderService
  * Garante que todas as operações sejam executadas e em caso de erro de estoque insuficiente um rollbackautomtico é executado.


## Testes

Testes Unitários para regras de estoque
### Cenarios
* Pedido com estoque suficiente
* Falha por estoque insuficiente
* Calculo correto de totais
* Multiplos produtos sem estoque
* Produto inexistente