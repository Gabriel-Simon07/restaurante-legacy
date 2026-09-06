---
name: avaliador-refatoracao
description: Avalia o código após refatorações no projeto restaurante-legacy, verifica a eliminação de code smells (Clean Code, Refactoring, GoF Patterns), atualiza o status no dashboard/smells-status.json e regera o dashboard de métricas e cobertura.
---

# 🕵️ Skill: Avaliador de Refatoração & Atualizador do Dashboard

Esta skill é ativada quando o usuário solicita avaliar uma refatoração no projeto `restaurante-legacy`, conferir se uma dívida técnica do PDI foi sanada ou atualizar o dashboard de qualidade após uma melhoria no código.

---

## 📋 Fluxo de Execução da Skill

Ao ser acionado para avaliar uma refatoração ou atualizar o status de um smell:

### Passo 1: Inspecionar as Mudanças no Código
1. Verifique os arquivos modificados ou novos arquivos criados em `src/main/java/com/restaurante/`.
2. Compare com o catálogo de smells catalogados em `dashboard/smells-status.json` e as metas em `pdi.md`:
   - **Strategy Smell**: Verifique se foi criada a interface `FormaPagamentoStrategy` (ou equivalente) e classes concretas para Dinheiro, Cartão e PIX, eliminando o switch-case gigante de `processarTudoEPagar`.
   - **State Smell**: Verifique se o ciclo de vida do pedido agora utiliza o padrão State (ex: `PedidoState`) eliminando o controle por int `p_st` e booleans.
   - **Observer Smell**: Verifique se a Cozinha virou um Subject com listeners/observers desacoplados.
   - **Template Method Smell**: Verifique se o preparo de itens Cozinha/Bar herdou de um template abstrato.
   - **God Class**: Verifique se `RestauranteLegado` foi dividida em classes coesas (`MesaService`, `CardapioService`, `CozinhaService`, etc.).
   - **Long Method**: Verifique se `processarTudoEPagar` foi quebrado em métodos menores com responsabilidade única.
   - **Nomes Ruins**: Verifique se `r_n`, `p_st`, `m1`, `cli`, `val_tot_aux` foram renomeados para nomes expressivos.
   - **Primitive Obsession**: Verifique se foram criados Value Objects (`Dinheiro`, `NumeroMesa`, etc.).
   - **Códigos de Erro**: Verifique se strings como `"ERRO_MESA_OCUPADA"` foram substituídas por Exceções de Domínio.

---

### Passo 2: Validar a Rede de Segurança (Testes Automatizados)
Execute a suíte de testes unitários para garantir que a refatoração preservou o comportamento funcional do restaurante (0 regressões):

```powershell
mvn test
```

> ⚠️ Se algum teste falhar, **não marque o smell como resolvido**! Aponte exatamente o teste que quebrou e sugira o ajuste ao usuário.

---

### Passo 3: Atualizar o Status do Smell no `dashboard/smells-status.json`
Com os testes passando e a refatoração comprovada:
1. Abra [dashboard/smells-status.json](file:///c:/projetos/projetos/java/restaurante-legacy/dashboard/smells-status.json).
2. Localize o objeto correspondente ao `id` do smell refatorado (ex: `"id": "strategy-smell"`).
3. Altere o campo `"status"` de `"DETECTADO (LEGADO)"` para `"REFATORADO"`.

---

### Passo 4: Atualizar Diretamente o `dashboard/index.html`
1. Leia as métricas mais recentes de cobertura em [target/site/jacoco/jacoco.xml](file:///c:/projetos/projetos/java/restaurante-legacy/target/site/jacoco/jacoco.xml).
2. Leia o status atualizado de todos os smells em [dashboard/smells-status.json](file:///c:/projetos/projetos/java/restaurante-legacy/dashboard/smells-status.json).
3. Atualize o arquivo [dashboard/index.html](file:///c:/projetos/projetos/java/restaurante-legacy/dashboard/index.html) usando `replace_file_content` para:
   - Atualizar a data e hora do dashboard no cabeçalho;
   - Atualizar o badge do smell refatorado de `<span class="badge badge-detectado">DETECTADO (LEGADO)</span>` para `<span class="badge badge-resolvido">REFATORADO</span>`;
   - Atualizar os números de cobertura de linhas/classes caso tenham variado;
   - Atualizar o contador de smells resolvidos.

---

### Passo 5: Feedback Claro para o PDI do Usuário
Apresente ao usuário:
1. **Smell Refatorado**: Nome do smell e padrão/técnica aplicada com sucesso.
2. **Evidência**: Cobertura de testes atualizada e confirmação de 100% dos testes passando (`mvn test`).
3. **Dashboard Atualizado**: Confirme que o `dashboard/index.html` foi atualizado e que o badge está verde (`REFATORADO`).
4. **Instrução para Visualização**:
   ```powershell
   Start-Process dashboard/index.html
   ```
5. **Próxima Ação do PDI**: Indique qual é a próxima ação listada no [pdi.md](file:///c:/projetos/projetos/java/restaurante-legacy/pdi.md).
