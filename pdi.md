# 🎯 Plano de Desenvolvimento Individual (PDI) — Laboratório "Bella Tavola"

> **Contexto & Origem:** Projeto desenvolvido como laboratório prático para PDI de Engenharia de Software. A base do código "legado funcional", a suíte de testes unitários e o gerador do Dashboard de Qualidade/Cobertura foram **estruturados com o auxílio de Inteligência Artificial**. O objetivo é servir como ambiente controlado para praticar **Clean Code**, **Refactoring** e **Design Patterns (GoF)**, monitorando o avanço pelo dashboard.

---

## 📌 Metas e Ações do PDI

### 🔹 Ação 1: Higienização de Nomes e Eliminação de Códigos de Erro
- **O que fazer:** Renomear variáveis enigmáticas (`r_n`, `p_st`, `m1`, `cli`, `val_tot_aux`) e substituir retornos como `"ERRO_MESA_OCUPADA"` e `null` por Exceções de Domínio (`MesaOcupadaException`, etc.).
- **Resultado Esperado:** 100% dos testes unitários passando e smell *Nomes Ruins* marcado como resolvido no dashboard.
- **Data Limite:** 15/09/2026

---

### 🔹 Ação 2: Decomposição do Método Monstruoso de Fechamento
- **O que fazer:** Aplicar *Extract Method* no método `processarTudoEPagar(...)` (120+ linhas), separando cálculo de taxas, cupons, impressão de cupom e atualização de estado.
- **Resultado Esperado:** Redução de complexidade, métodos com responsabilidade única e print do dashboard comprovando a eliminação do smell *Long Method*.
- **Data Limite:** 22/09/2026

---

### 🔹 Ação 3: Eliminação de Flag Arguments e Construtores Telescópicos
- **O que fazer:** Refatorar `abrirAtendimento(...)` (8 parâmetros primitivos e flags booleanas) introduzindo um Parameter Object ou padrão de criação.
- **Resultado Esperado:** Assinatura de método limpa, código legível e print do dashboard com smell *Flag Arguments* mitigado.
- **Data Limite:** 29/09/2026

---

### 🔹 Ação 4: Quebra da God Class (`RestauranteLegado`)
- **O que fazer:** Aplicar *Extract Class* para decompor a classe `RestauranteLegado` em serviços coesos (`MesaService`, `CardapioService`, `CozinhaService` e `CaixaService`).
- **Resultado Esperado:** Gráfico de distribuição de LOC do dashboard equilibrado (nenhuma classe concentrando mais de 150 linhas) e testes íntegros.
- **Data Limite:** 06/10/2026

---

### 🔹 Ação 5: Eliminação de Primitive Obsession & Feature Envy
- **O que fazer:** Criar Value Objects (`Dinheiro`, `NumeroMesa`) e mover a lógica contábil de fechamento para dentro da entidade `Pedido` (*Move Method*).
- **Resultado Esperado:** Tipagem forte no domínio, eliminação de `double` solto para dinheiro e print do dashboard com smells de *Refactoring* resolvidos.
- **Data Limite:** 13/10/2026

---

### 🔹 Ação 6: Implementação do Padrão Strategy para Formas de Pagamento
- **O que fazer:** Substituir a estrutura condicional rígida (`if/else` de Dinheiro, Cartão e PIX) pela interface `FormaPagamentoStrategy` e classes concretas.
- **Resultado Esperado:** Código aberto para extensão (OCP), novos meios de pagamento adicionados sem alterar o checkout e print do dashboard atualizado.
- **Data Limite:** 20/10/2026

---

### 🔹 Ação 7: Implementação do Padrão State para o Ciclo de Vida do Pedido
- **O que fazer:** Substituir as flags e inteiros soltos de status (`p_st`, `cancelado`, `pago`) pelo padrão **State (GoF)** (`AbertoState`, `EmPreparoState`, `ProntoState`, `EntregueState`, `PagoState`).
- **Resultado Esperado:** Transições de estado explícitas e seguras, eliminação de validações aninhadas e print do dashboard comprovando a refatoração.
- **Data Limite:** 27/10/2026

---

### 🔹 Ação 8: Implementação do Padrão Observer na Linha de Produção
- **O que fazer:** Desacoplar a Cozinha e o Bar do garçom, transformando a Cozinha em um `Subject` que dispara eventos para múltiplos observadores (`GarcomListener`, `PainelPedidosListener`).
- **Resultado Esperado:** Cozinha 100% desacoplada de atores externos, testes de integração passando e print final do dashboard com radar de maturidade no nível máximo.
- **Data Limite:** 03/11/2026

---

## 🔄 Fluxo de Validação & Evidências para o PDI

1. **Implemente a refatoração** no código Java correspondente à meta da semana.
2. **Solicite ao agente no chat**:
   > *"Avalie a refatoração da Ação X e atualize o dashboard."*
3. **A Skill `avaliador-refatoracao` irá**:
   - Inspecionar se o padrão/técnica foi aplicado corretamente no código;
   - Executar `mvn test` para garantir 0 regressões (100% testes verdes);
   - Marcar o smell como `"REFATORADO"` no `dashboard/smells-status.json`;
   - Atualizar diretamente o `dashboard/index.html`.
4. **Capture a evidência**: Abra o dashboard no navegador (`Start-Process dashboard/index.html`) e tire o print com a badge verde e a cobertura atualizada para anexar ao seu PDI.
