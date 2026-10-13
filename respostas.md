# Respostas sobre SQLite em aplicações mobile

## 1. Vantagem do SQLite em comparação ao armazenamento somente em memória

A principal vantagem do SQLite em relação ao armazenamento somente em memória é a **persistência de dados**. Enquanto a memória volátil perde todos os dados ao fechar o aplicativo ou reiniciar o dispositivo, o SQLite grava os dados permanentemente no armazenamento interno do dispositivo.

No contexto deste projeto, note-se que o `ProdutoDbHelper` estende `SQLiteOpenHelper` com `DATABASE_VERSION = 2`, e o código em `MainActivity` chama `atualizarLista()` tanto no `onCreate` quanto após cada inserção. Isso garante que, ao fechar e reabrir o app (como testado no checklist item 17), os produtos permanecem disponíveis na lista. Dados em memória seriam perdidos imediatamente, tornando o aplicativo inadequado para cadastros que precisam sobreviver ao fechamento. Além disso, o SQLite permite consultas complexas, ordering (`ORDER BY ordem ASC`), e relações de dados estruturados, algo que estruturas simples em memória não suportam eficientemente.

## 2. O que seria mais indicado para aplicações que requerem sincronização com dados online

Para aplicações que requerem sincronização com dados online, o padrão mais indicado seria o **padrão de cache local + sincronização em background**. O SQLite seria usado para armazenar os dados localmente, garantindo que o aplicativo funcione offline e sincronize com o servidor quando conexão disponível.

No entanto, usando apenas `SQLiteOpenHelper` local, não há mecanismo automático de sincronização. O mais indicado seria:

- Manter o SQLite local para operação offline e desempenho
- Implementar um serviço de sincronização que compara os registros locais (usando `DATABASE_VERSION` ou timestamps) com o servidor
- Enviar alterações do cliente para o servidor (INSERT/UPDATE via API REST)
- Baixar novas alterações do servidor para o cliente
- Lidar com conflitos de sincronização quando ambos forem modificados offline

Para este projeto, que atualmente é apenas interface, o próximo passo seria implementar a classe `ProdutoDbHelper` completa e então adicionar uma camada de sincronização, talvez usando `AsyncTask` ou coroutines para não bloquear a UI, verificando conectividade antes de sincronizar. O ideal seria ter uma estratégia de "offline-first": o usuário opera no SQLite local, e quando há conexão, os dados são sincronizados com a API backend, que por sua vez pode usar seu próprio banco de dados (PostgreSQL, MySQL, etc.).

## 3. Este arquivo precisa estar no gitignore?

**Não, o `respostas.md` não deve estar no `.gitignore`.**

Diferente de arquivos gerados automaticamente (`local.properties`, `build/`, `.gradle/`), este arquivo contém conteúdo intencional — respostas reflexivas sobre o projeto, feitas como parte do trabalho sobre "frameworks para desenvolvimento de software". Arquivos de documentação e respostas acadêmicas/profissionais devem versionados no controle de versão para registar o processo de tomada de decisão e aprendizado. O `.gitignore` deve conter apenas arquivos que variam por ambiente de desenvolvedor ou são gerados durante o build, não conteúdos educacionais ou documentativos.