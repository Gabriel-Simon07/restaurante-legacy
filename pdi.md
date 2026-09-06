# 🎯 Plano de Desenvolvimento Individual (PDI) — Laboratório "Bella Tavola"

> **Contexto & Origem:** Projeto desenvolvido como laboratório prático para PDI de Engenharia de Software. A base do código "legado funcional", a suíte de testes unitários e o gerador do Dashboard de Qualidade/Cobertura foram **estruturados com o auxílio de Inteligência Artificial**. O objetivo é servir como ambiente controlado para praticar **Clean Code**, **Refactoring** e **Design Patterns (GoF)**, monitorando o avanço pelo dashboard.

---

## 📌 Metas e Ações do PDI

> 🌿 **Estratégia de Branches:** A cada objetivo/ação, deve ser criada uma branch com a data limite no formato `refc-DD_MM_AAAA` (exemplo: `refc-05_09_2026`). Todo o desenvolvimento e validação de cada ação deve ser realizado em sua branch correspondente antes do merge na `main`.

### 🔹 Ação 1: Higienização de Nomes e Eliminação de Códigos de Erro
- **Branch:** `refc-15_09_2026`
- **Data Limite:** 15/09/2026
- **O que fazer:** Renomear variáveis enigmáticas (`r_n`, `p_st`, `m1`, `cli`, `val_tot_aux`) e substituir retornos como `"ERRO_MESA_OCUPADA"` e `null` por Exceções de Domínio (`MesaOcupadaException`, etc.).
- **Resultado Esperado:** 100% dos testes unitários passando e smell *Nomes Ruins* marcado como resolvido no dashboard.

---

### 🔹 Ação 2: Decomposição do Método Monstruoso de Fechamento
- **Branch:** `refc-22_09_2026`
- **Data Limite:** 22/09/2026
- **O que fazer:** Aplicar *Extract Method* no método `processarTudoEPagar(...)` (120+ linhas), separando cálculo de taxas, cupons, impressão de cupom e atualização de estado.
- **Resultado Esperado:** Redução de complexidade, métodos com responsabilidade única e print do dashboard comprovando a eliminação do smell *Long Method*.

---

### 🔹 Ação 3: Eliminação de Flag Arguments e Construtores Telescópicos
- **Branch:** `refc-29_09_2026`
- **Data Limite:** 29/09/2026
- **O que fazer:** Refatorar `abrirAtendimento(...)` (8 parâmetros primitivos e flags booleanas) introduzindo um Parameter Object ou padrão de criação.
- **Resultado Esperado:** Assinatura de método limpa, código legível e print do dashboard com smell *Flag Arguments* mitigado.

---

### 🔹 Ação 4: Quebra da God Class (`RestauranteLegado`)
- **Branch:** `refc-06_10_2026`
- **Data Limite:** 06/10/2026
- **O que fazer:** Aplicar *Extract Class* para decompor a classe `RestauranteLegado` em serviços coesos (`MesaService`, `CardapioService`, `CozinhaService` e `CaixaService`).
- **Resultado Esperado:** Gráfico de distribuição de LOC do dashboard equilibrado (nenhuma classe concentrando mais de 150 linhas) e testes íntegros.

---

### 🔹 Ação 5: Eliminação de Primitive Obsession & Feature Envy
- **Branch:** `refc-13_10_2026`
- **Data Limite:** 13/10/2026
- **O que fazer:** Criar Value Objects (`Dinheiro`, `NumeroMesa`) e mover a lógica contábil de fechamento para dentro da entidade `Pedido` (*Move Method*).
- **Resultado Esperado:** Tipagem forte no domínio, eliminação de `double` solto para dinheiro e print do dashboard com smells de *Refactoring* resolvidos.

---

### 🔹 Ação 6: Implementação do Padrão Strategy para Formas de Pagamento
- **Branch:** `refc-20_10_2026`
- **Data Limite:** 20/10/2026
- **O que fazer:** Substituir a estrutura condicional rígida (`if/else` de Dinheiro, Cartão e PIX) pela interface `FormaPagamentoStrategy` e classes concretas.
- **Resultado Esperado:** Código aberto para extensão (OCP), novos meios de pagamento adicionados sem alterar o checkout e print do dashboard atualizado.

---

### 🔹 Ação 7: Implementação do Padrão State para o Ciclo de Vida do Pedido
- **Branch:** `refc-27_10_2026`
- **Data Limite:** 27/10/2026
- **O que fazer:** Substituir as flags e inteiros soltos de status (`p_st`, `cancelado`, `pago`) pelo padrão **State (GoF)** (`AbertoState`, `EmPreparoState`, `ProntoState`, `EntregueState`, `PagoState`).
- **Resultado Esperado:** Transições de estado explícitas e seguras, eliminação de validações aninhadas e print do dashboard comprovando a refatoração.

---

### 🔹 Ação 8: Implementação do Padrão Observer na Linha de Produção
- **Branch:** `refc-03_11_2026`
- **Data Limite:** 03/11/2026
- **O que fazer:** Desacoplar a Cozinha e o Bar do garçom, transformando a Cozinha em um `Subject` que dispara eventos para múltiplos observadores (`GarcomListener`, `PainelPedidosListener`).
- **Resultado Esperado:** Cozinha 100% desacoplada de atores externos, testes de integração passando e print final do dashboard com radar de maturidade no nível máximo.

---

## 🔄 Fluxo de Validação & Evidências para o PDI

1. **Crie a branch correspondente ao objetivo da semana**:
   ```bash
   git checkout -b refc-DD_MM_AAAA
   ```
2. **Implemente a refatoração** no código Java correspondente à meta da semana.
3. **Solicite ao agente no chat**:
   > *"Avalie a refatoração da Ação X e atualize o dashboard."*
4. **A Skill `avaliador-refatoracao` irá**:
   - Inspecionar se o padrão/técnica foi aplicado corretamente no código;
   - Executar `mvn test` para garantir 0 regressões (100% testes verdes);
   - Marcar o smell como `"REFATORADO"` no `dashboard/smells-status.json`;
   - Atualizar diretamente o `dashboard/index.html`.
5. **Capture a evidência**: Abra o dashboard no navegador (`Start-Process dashboard/index.html`) e tire o print com a badge verde e a cobertura atualizada para anexar ao seu PDI.
6. **Integração**: Concluída e validada a ação, faça o merge da branch para a `main`.
