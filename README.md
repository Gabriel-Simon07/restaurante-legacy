# 🍝 Restaurante "Bella Tavola" — Laboratório de Refatoração & Clean Code

> Sistema executável de gestão de pedidos de restaurante, deliberadamente implementado com **dívidas técnicas, "code smells" e alto acoplamento**, servindo como **laboratório prático de estudos** para aplicar conceitos de **Clean Code**, **Refactoring** e **Design Patterns (GoF)** sem medo de quebrar o negócio.

---

## 🎯 1. Objetivo do Projeto: Para Que Serve?

Na rotina de desenvolvimento, raramente encontramos sistemas perfeitos. A maioria dos desenvolvedores lida com **sistemas legados**: códigos com classes gigantes, métodos de centenas de linhas, variáveis com nomes abreviados, regras de cálculo misturadas com apresentação e alta fragilidade a mudanças.

Este projeto foi construído propositalmente como um **"Legado Funcional"**:
- **100% Funcional**: O sistema funciona de ponta a ponta, calcula impostos, descontos, rateio, processa pedidos na cozinha e bar, e emite comprovantes fiscais no terminal.
- **Rede de Segurança (Safety Net)**: Possui uma suíte completa de **testes de regressão automatizados com JUnit 5** cobrindo todas as regras de negócio.
- **Ambiente Seguro de Treinamento**: Você pode renomear variáveis, quebrar a *God Class*, mover métodos e plugar padrões de projeto (GoF) com a tranquilidade de que, ao rodar `mvn test`, saberá imediatamente se alterou o comportamento do software.

---

## 📊 2. Dashboard de Qualidade, Cobertura & Code Smells

O projeto conta com um **Dashboard Visual Moderno** em HTML/CSS (Dark Mode com **Chart.js** e integração direta com o relatório oficial do **JaCoCo**).

### 🖥️ O que o Dashboard exibe:
1. **Cobertura Real de Testes (JaCoCo)**: Percentual de linhas, branches e métodos cobertos pelos testes unitários, com gráfico Donut interativo e link para o relatório minucioso do JaCoCo (`target/site/jacoco/index.html`).
2. **Distribuição de Linhas por Classe (LOC)**: Gráfico em barras destacando visualmente a assimetria da *God Class* (`RestauranteLegado.java`) frente às demais classes.
3. **Radar de Maturidade Arquitetural**: Comparativo do estado atual (Legado) contra a meta pretendida pós-refatoração (Clean Code, SOLID, GoF, Desacoplamento).
4. **Inventário Interativo de Code Smells**: Tabela dinâmica com filtros por categoria (**Clean Code**, **Refactoring**, **Design Patterns GoF**), detalhando:
   - Nome do Smell e descrição do problema
   - Localização no código-fonte (arquivo e linha/método afetado)
   - Severidade (Crítica, Alta, Média)
   - Status (`DETECTADO / LEGADO` vs `REFATORADO / RESOLVIDO`)
   - Sugestão de refatoração recomendada

---

## 🚀 3. Como Executar o Projeto

### Pré-requisitos
- **Java 17+** (OpenJDK ou Eclipse Adoptium)
- **Apache Maven 3.8+**

### 🧪 A. Executar a Suíte de Testes Automatizados
Executa todos os testes de regressão do JUnit 5 e gera o relatório binário do JaCoCo:
```powershell
mvn test
```

---

### 📈 B. Visualizar e Atualizar o Dashboard de Qualidade
O arquivo `dashboard/index.html` exibe as métricas de cobertura (JaCoCo) e o status das dívidas técnicas:

- **Para abrir o dashboard no navegador**:
  ```powershell
  Start-Process dashboard/index.html
  ```

- **Para atualizar após uma refatoração do PDI**:
  Basta solicitar ao agente:
  > *"Avalie minha refatoração de [Ação do PDI] e atualize o dashboard."*
  
  A **Skill `avaliador-refatoracao`** irá:
  1. Inspecionar o código alterado;
  2. Executar `mvn test` para garantir 0 regressões;
  3. Atualizar o status em `dashboard/smells-status.json` para `REFATORADO`;
  4. Atualizar o arquivo `dashboard/index.html` com o novo badge verde e métricas atualizadas.

---

### 🍽️ C. Executar a Simulação do Restaurante no Terminal
Executa a simulação completa do restaurante "Bella Tavola" (`com.restaurante.Main`):
```powershell
mvn compile exec:java
```

**Cenários demonstrados na simulação:**
- **Cenário 1 (Mesa 3 - VIP)**: Pedido completo (Lasanha, Vinhos, Sobremesa com notas especiais), produção no Bar e Cozinha com notificações ao garçom, entrega na mesa, prévia com cupom `BELLA20` e fechamento com **PIX** (5% desconto extra) e rateio em 2 pessoas.
- **Cenário 2 (Mesa 5 - Almoço Executivo)**: Bife de Chorizo ao ponto com refrigerante gelo e limão, cupom `PROMO5`, pagamento em **Dinheiro** com cálculo automático de troco e liberação imediata da mesa.

---

## 💣 4. Catálogo de Code Smells e Roteiro de Refatoração

| Code Smell / Dívida Técnica | Onde está no Código | Categoria | Refatoração Alvo |
| :--- | :--- | :--- | :--- |
| **God Class (Large Class)** | `RestauranteLegado.java` (450+ LOC) | *Refactoring* | *Extract Class*: Quebrar em `Mesa`, `Cardapio`, `CozinhaService`, `CaixaService`. |
| **Nomes Ruins e Abreviações** | `r_n`, `p_st`, `m1`, `cli`, `val_tot_aux` | *Clean Code* | Renomear para nomes expressivos (`statusPedido`, `totalComTaxa`, `nomeCliente`). |
| **Método Monstruoso (Long Method)** | `processarTudoEPagar(...)` (120+ LOC) | *Clean Code* | *Extract Method*: Decompor em métodos pequenos com responsabilidade única. |
| **Níveis de Abstração Misturados** | `processarTudoEPagar` / `imprimirPreviaComanda` | *Clean Code* | Isolar a camada de apresentação em uma classe de visualização/impressão (CLI). |
| **Flag Arguments & Construtor Inchado** | `abrirAtendimento(...)` com 8 parâmetros | *Clean Code* | Introduzir Parameter Object ou padrão *Builder*. |
| **Retorno de Códigos de Erro** | Retornos `"ERRO_MESA_OCUPADA"`, `null`, `-1` | *Clean Code* | Substituir por exceções de domínio expressivas (`MesaOcupadaException`, etc.). |
| **Primitive Obsession** | Preço `double`, Mesa `int`, Status `int` | *Refactoring* | Criar Value Objects: `Dinheiro`, `NumeroMesa`, `StatusPedido`. |
| **Duplicated Code (Código Duplicado)** | Regras de taxa (10%) e cupom em 2 métodos | *Refactoring* | Centralizar regras contábeis em um único ponto. |
| **Feature Envy (Inveja de Recursos)** | Checkout lendo dezenas de getters de `Pedido` | *Refactoring* | *Move Method*: Mover os cálculos para dentro da entidade `Pedido`. |
| **Strategy Smell** | Switch-case/if-else gigante para pagamentos | *GoF Pattern* | Implementar **Strategy Pattern** (`FormaPagamentoStrategy` para Dinheiro, Cartão, Pix). |
| **Observer Smell** | Cozinha chamando garçom diretamente acoplada | *GoF Pattern* | Implementar **Observer Pattern** (Cozinha como `Subject` notificando ouvintes). |
| **Template Method Smell** | Passos idênticos duplicados em Cozinha e Bar | *GoF Pattern* | Implementar **Template Method** com classe abstrata `PreparoItemTemplate`. |
| **State Smell** | Status mantido por inteiros e flags booleanas | *GoF Pattern* | Implementar **State Pattern** (`PedidoState` com transições formais de estado). |

---

## 📁 5. Estrutura do Projeto

```
restaurante-legacy/
├── pom.xml                                  # Configuração Maven, Java 17, JUnit 5, JaCoCo e Exec
├── README.md                                # Documentação completa do projeto
├── pdi.md                                   # Plano de Desenvolvimento Individual (8 ações práticas)
├── plan.md                                  # Plano arquitetural e catálogo de code smells
├── .agents/
│   └── skills/
│       └── avaliador-refatoracao/           # Skill Antigravity para avaliar refatorações e atualizar o dashboard
├── dashboard/
│   ├── index.html                           # Dashboard visual moderno (Chart.js + JaCoCo)
│   └── smells-status.json                   # Persistência do status de cada dívida técnica
└── src/
    ├── main/
    │   └── java/
    │       └── com/restaurante/
    │           ├── Main.java                # Ponto de entrada (simulação completa dos cenários)
    │           ├── RestauranteLegado.java   # A "God Class" com as dívidas técnicas intencionais
    │           ├── Item.java                # Entidade de item do cardápio e pedido
    │           ├── Garcom.java              # Entidade do atendente acoplada à cozinha
    │           └── Pedido.java              # Comanda da mesa com múltiplos status
    └── test/
        └── java/
            └── com/restaurante/
                └── RestauranteTest.java     # Suíte com 10 testes de regressão unitários (JUnit 5)
```

---

## 🗺️ 6. Próximos Passos de Estudo

1. **Etapa 2 — Clean Code**:
   - [ ] Renomear identificadores enigmáticos para nomes expressivos.
   - [ ] Reduzir tamanho dos métodos (métodos com responsabilidade única).
   - [ ] Eliminar flags booleanas e parâmetros soltos.
   - [ ] Substituir códigos de retorno por exceções de domínio.
2. **Etapa 3 — Refactoring**:
   - [ ] Quebrar a `God Class` em classes coesas (`Mesa`, `Cardapio`, `CozinhaService`, `CaixaService`).
   - [ ] Eliminar `Feature Envy` movendo métodos para `Pedido`.
   - [ ] Introduzir Value Objects para eliminar `Primitive Obsession`.
3. **Etapa 4 — Design Patterns (GoF)**:
   - [ ] **Strategy**: Formas de pagamento (Dinheiro, Cartão, Pix).
   - [ ] **State**: Ciclo de vida do pedido (`AbertoState`, `EmPreparoState`, `ProntoState`, etc.).
   - [ ] **Observer**: Notificações desacopladas Cozinha ➔ Garçom/Painel.
   - [ ] **Template Method**: Linha de produção padronizada Cozinha vs Bar.
