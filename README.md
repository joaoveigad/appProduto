# App Produto — Cadastro de Produtos com SQLite (Android / Java)

Software desenvolvido como trabalho referente ao tema **frameworks para desenvolvimento de software**. Aplicativo Android nativo em **Java** para cadasrar produtos e persistir os dados em **SQLite**. O objetivo é aprender o funcionamento direto do SQLite no Android, sem Room, RecyclerView, Retrofit, API REST ou arquitetura complexa.

> **Status atual: etapa de INTERFACE apenas.**
> Tela (formulário + lista) com validação de campos. Botão SALVAR mostra Toast provisório.
> Ainda não existem as classes `Produto.java` e `ProdutoDbHelper.java` nem o banco de dados implementados.

## Funcionamento

O software apresenta um formulário com dois campos de entrada:
- **Nome**: deve ter no mínimo 3 caracteres
- **Preço**: deve ser um número maior que zero

Ao tentar salvar o produto, validam-se os campos na seguinte ordem:
1. **Nome inválido** (menos de 3 caracteres): mensagem em vermelho aparece acima do campo Nome
2. **Preço vazio**: mensagem "Preço inválido. Informe um valor maior que zero." em vermelho aparece acima do campo Preço
3. **Preço não é número** (ex.: "abc"): mensagem "deve ser um número" em vermelho aparece acima do campo Preço
4. **Preço zero ou negativo**: mensagem "Preço inválido. Informe um valor maior que zero." em vermelho aparece acima do campo Preço
5. **Todos os campos válidos**: produto é salvo no SQLite, campos são limpos e aparece Toast de sucesso

As mensagens de erro aparecem em vermelho graças ao `TextInputLayout`, que pinta a borda do campo e exibe o texto abaixo em cor de erro. Nenhum dado inválido chega ao banco de dados graças aos `return` que interrompem o método `salvarProduto()` ao primeiro erro.