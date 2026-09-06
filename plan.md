# 📋 Plano de Projeto: Gestão de Restaurante "Bella Tavola"
> **Objetivo:** Criar um sistema funcional de gestão de pedidos de restaurante, ponta a ponta, deliberadamente implementado com **dívidas técnicas, "code smells" e acoplamento**, servindo como laboratório prático para aplicar os conceitos de **Clean Code**, **Refactoring** e **Design Patterns (GoF)**.

---

## 🎯 1. Visão Geral do Domínio

O sistema cobre o ciclo de vida completo de uma operação de restaurante:

```
[Cadastro do Restaurante & Mesas] 
       │
       ▼
[Cardápio & Montagem do Pedido] 
       │
       ▼
[Envio para Cozinha & Fila de Preparo] 
       │
       ▼
[Notificação & Entrega na Mesa] 
       │
       ▼
[Fechamento de Conta & Pagamento]
```

### 🍔 Fluxo do Negócio
1. **Restaurante e Ambiente**: Configuração do restaurante (nome, mesas disponíveis, garçons).
2. **Cardápio**: Pratos, bebidas, sobremesas e adicionais com preços e tempos estimados de preparo.
3. **Mesa e Pedido**: Cliente senta à mesa; garçom abre o pedido e adiciona itens com observações (ex: "sem cebola", "ponto da carne", "gelo e limão").
4. **Cozinha**: Recebe o pedido, altera status para *Em Preparo*, depois *Pronto*.
5. **Entrega**: Garçom é notificado, retira o prato e entrega na mesa (*Entregue*).
6. **Fechamento**: Cálculo de subtotal, adicionais, taxa de serviço (10%), descontos/cortesias e formas de pagamento (Dinheiro, Cartão, PIX).

---

## ⚙️ 2. Requisitos Funcionais (O Sistema DEVE Funcionar!)

O software deve ser 100% executável e produzir os resultados corretos, para que as refatorações possam ser validadas com segurança.

- **RF01 - Configuração**: Criar restaurante com N mesas e carregar cardápio inicial.
- **RF02 - Atendimento de Mesa**: Ocupar mesa, associar cliente e garçom.
- **RF03 - Lançamento de Itens**: Adicionar itens com quantidades e observações personalizadas.
- **RF04 - Linha de Produção da Cozinha**:
  - Distribuir itens (bebidas vão para o Bar, pratos quentes vão para a Cozinha).
  - Atualizar status (`RECEBIDO`, `EM_PREPARO`, `PRONTO`).
- **RF05 - Notificação de Entrega**: Registrar quando o item/pedido foi levado até a mesa (`ENTREGUE`).
- **RF06 - Faturamento e Checkout**:
  - Fechar conta com diferentes opções de rateio ou formas de pagamento.
  - Aplicar regras de taxa de serviço e cupons promocionais.
  - Liberar mesa para o próximo cliente.

---

## 💣 3. Catálogo de Dívidas Técnicas & Code Smells Intencionais

Esta seção detalha os "problemas" que serão propositalmente embutidos no código inicial, e como eles se relacionam com o seu estudo:

### 📗 A. Para praticar *Clean Code* (Robert C. Martin)

| Problema / Smell | Onde estará no código | Como resolver depois |
| :--- | :--- | :--- |
| **Nomes Ruins e Abreviações** | Variáveis como `r_n`, `p_st`, `m1`, `val_tot_aux`, `flag_pg` | Renomear para nomes que revelam intenção (`totalComTaxa`, `statusPedido`). |
| **Métodos Monstruosos (Long Methods)** | Um método `processarTudoEPagar()` com 120+ linhas fazendo validação, cálculo, print e persistência | Dividir em métodos pequenos com responsabilidade única. |
| **Níveis de Abstração Misturados** | Regras de cálculo de impostos e taxas misturadas com `System.out.println` e formatação de texto | Separar lógica de domínio da camada de apresentação/IO. |
| **Muitos Parâmetros (Flag Arguments)** | `criarPedido(int mesa, String cliente, boolean vip, boolean entrega, int tipo, double desc, String obs, int garcomId)` | Introduzir objetos de parâmetro ou padrões de criação. |
| **Retorno de Códigos de Erro / Flags nulas** | Métodos retornando `-1`, `null` ou strings de erro tipo `"ERRO_04"` em vez de Exceptions | Criar exceções de domínio expressivas (`MesaOcupadaException`, `ItemEsgotadoException`). |
| **Acoplamento Forte com I/O (`Scanner`)** | `Scanner` passado como parâmetro em métodos de negócio (`fazerPedido(Scanner sc)`), misturando leitura de teclado (`sc.nextInt()`), tratamento de quebra de linha e lógica de cálculo | Isolar a camada de apresentação/CLI em uma classe de controle, deixando o domínio puro e testável sem depender do teclado. |
| **Comentários Ruins / Desnecessários** | Comentários repetindo o código (`// soma 10% da taxa`) mascarando código mal escrito | Tornar o código autoexplicativo e remover comentários parasitas. |

---

### 📘 B. Para praticar *Refactoring* (Martin Fowler)

| Code Smell | Sintoma no Código Inicial | Refatoração Alvo |
| :--- | :--- | :--- |
| **God Class (Large Class)** | Uma classe `RestauranteManager` que cuida do cardápio, estoque, mesas, garçons, faturamento e cozinha | *Extract Class*: Quebrar em `Mesa`, `Cardapio`, `CozinhaService`, `CaixaService`. |
| **Primitive Obsession** | Dinheiro tratado como `double` simples; Endereço/Mesa como `int` ou `String` soltos | *Replace Data Value with Object*: Criar Value Objects (`Dinheiro`, `NumeroMesa`). |
| **Feature Envy (Inveja de Recursos)** | A classe de pagamento acessa 8 getters da classe `Pedido` para calcular totais | *Move Method*: Mover o cálculo para onde os dados residem (`Pedido`). |
| **Data Clumps (Aglomerados de Dados)** | Parâmetros `(itemNome, itemPreco, categoria, quantidade)` trafegando juntos em várias assinaturas | *Preserve Whole Object* / *Introduce Parameter Object*. |
| **Duplicated Code** | Regra de aplicação de desconto duplicada no fechamento de conta e na prévia da comanda | *Extract Method* / Centralização de lógica. |

---

### 📙 C. Ganchos Prontos para *Design Patterns* (GoF)

O código inicial usará abordagens estruturadas/imperativas cheias de `if`/`switch`, servindo de trampolim perfeito para os padrões GoF:

1. **State Pattern (Comportamento)**:
   - *Estado Atual (Ruim)*: Pedido gerenciado por múltiplas variáveis booleanas e inteiros (`int status = 1; boolean cancelado = false; boolean pago = false;`). Se tentar entregar um pedido cancelado, vários `if` aninhados tentam validar.
   - *Padrão GoF*: Implementar `PedidoState` com transições claras (`AbertoState`, `EmPreparoState`, `ProntoState`, `EntregueState`, `FinalizadoState`).

2. **Strategy Pattern (Comportamento)**:
   - *Estado Atual (Ruim)*: Um switch-case gigante para processar pagamento:
     ```java
     if (tipo == 1) { /* dinheiro com troco */ }
     else if (tipo == 2) { /* cartao com taxa de maquininha */ }
     else if (tipo == 3) { /* pix com desconto de 5% */ }
     ```
   - *Padrão GoF*: Interface `FormaPagamentoStrategy` com implementações `PagamentoDinheiro`, `PagamentoCartao`, `PagamentoPix`.

3. **Observer Pattern (Comportamento)**:
   - *Estado Atual (Ruim)*: Quando a cozinha termina o prato, ela chama diretamente o garçom no código acoplado (`garcom.avisarPratoPronto()`), impedindo adicionar um painel visual ou campainha sem mexer na cozinha.
   - *Padrão GoF*: Cozinha como `Subject` notificando observadores registrados (`PainelPedidos`, `GarcomNotifier`).

4. **Builder / Factory Method (Criação)**:
   - *Estado Atual (Ruim)*: Construtores com 10 parâmetros, passando vários `null` para itens que não têm adicionais ou observações.
   - *Padrão GoF*: `PedidoBuilder` para construir pedidos complexos e `CardapioFactory` para instanciar tipos de itens (Bebida, PratoPrincipal, Sobremesa).

5. **Template Method (Comportamento)**:
   - *Estado Atual (Ruim)*: O fluxo de preparo de comida e bebida tem passos comuns (receber, validar ingredientes, preparar, empratar/envasar, sinalizar), mas o código hoje repete a estrutura com ligeiras variações.
   - *Padrão GoF*: Classe abstrata `PreparoItemTemplate`.

---

## 🏗️ 4. Arquitetura e Estrutura do Projeto

### Escolha da Linguagem
- **Linguagem Recomendada**: **Java** (ex: Java 17 ou 21 com Maven ou Gradle), pois:
  - É a linguagem onde Clean Code, Refactoring e GoF se expressam com máxima clareza tipada.
  - Facilita o uso de ferramentas de refatoração de IDEs (renomeação segura, extração de métodos).
  - Pode rodar em console inicialmente e opcionalmente plugar no PostgreSQL que já está no seu `docker-compose.yaml`.

### Estrutura Proposta de Diretórios
```
restaurante-legacy/
├── pom.xml (ou build.gradle)
├── README.md
├── plan.md
└── src/
    ├── main/
    │   └── java/
    │       └── com/restaurante/
    │           ├── Main.java                # Ponto de entrada (simulação completa)
    │           ├── RestauranteLegado.java   # A "God Class" com as dívidas técnicas
    │           ├── Item.java
    │           ├── Pedido.java
    │           └── ...
    └── test/
        └── java/
            └── com/restaurante/
                └── RestauranteTest.java     # Suite de testes para garantir a "Rede de Segurança"
```

> 🛡️ **Regra de Ouro da Refatoração**: Você **nunca** refatora sem testes! O projeto terá uma suíte de testes cobrindo as regras de negócio para que, ao alterar o código, você tenha certeza de que nada quebrou.

---

## 🚀 5. Roteiro de Estudo e Execução Passo a Passo

### Etapa 1: Implementação do Código "Legado Funcional" ✅ (Concluída)
- [x] Construir o código executável com todos os "smells" e dívidas técnicas intencionais.
- [x] Criar a suíte de testes de regressão com JUnit 5 (10 testes cobrindo RF01 a RF06 com 100% de sucesso).
- [x] Integrar medição de cobertura de testes com JaCoCo (79,6% nas classes de negócio).
- [x] Criar Dashboard visual interativo em HTML (`dashboard/index.html`).
- [x] Configurar Skill do Antigravity (`avaliador-refatoracao`) para automação de validação e atualização do dashboard.

### Etapa 2: Estudo Prático de Clean Code (Ações 1 a 3 do PDI)
- [ ] Renomear identificadores enigmáticos para nomes expressivos.
- [ ] Reduzir tamanho dos métodos (eliminar Long Methods e decompor `processarTudoEPagar`).
- [ ] Eliminar flags booleanas e aglomerados de dados em assinaturas.
- [ ] Substituir códigos de retorno por exceções ricas de domínio.

### Etapa 3: Estudo Prático de Refactoring (Ações 4 e 5 do PDI)
- [ ] Quebrar a `God Class` em classes coesas (`MesaService`, `CardapioService`, `CozinhaService`, `CaixaService`).
- [ ] Eliminar `Feature Envy` movendo métodos para suas classes donas dos dados (`Pedido`).
- [ ] Introduzir Value Objects (`Dinheiro`, `NumeroMesa`) para eliminar `Primitive Obsession`.

### Etapa 4: Aplicação dos Padrões de Projeto GoF (Ações 6 a 8 do PDI)
- [ ] Implementar **Strategy** nos pagamentos e taxas (`FormaPagamentoStrategy`).
- [ ] Implementar **State** nos estágios do pedido (`PedidoState`).
- [ ] Implementar **Observer** para comunicação Cozinha ➔ Garçom/Mesa.
- [ ] Implementar **Template Method** ou **Builder** para criação e preparo padronizados.

---

## 📌 Próximos Passos

Consulte o arquivo **[pdi.md](file:///c:/projetos/projetos/java/restaurante-legacy/pdi.md)** para o cronograma detalhado das 8 ações práticas com datas limites e critérios de aceitação para o seu Plano de Desenvolvimento Individual.
