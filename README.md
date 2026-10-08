# App Produto — Cadastro de Produtos com SQLite (Android / Java)

Aplicativo Android nativo em **Java** para cadastrar produtos e persistir os dados em
**SQLite**. O objetivo é aprender o funcionamento direto do SQLite no Android, sem Room,
RecyclerView, Retrofit, API REST ou arquitetura complexa.

> **Status atual: etapa de INTERFACE apenas.**
> Já existe a tela (formulário + lista) e o botão mostra um `Toast` provisório.
> Ainda **não** existem as classes `Produto.java` e `ProdutoDbHelper.java` nem o banco.
> O detalhamento completo da atividade está em `Plano_Persistencia_SQLite_Android.md`
> (esse arquivo fica só na sua máquina, foi colocado no `.gitignore`).

---

## 1. Tecnologias

| Item | Versão usada neste projeto |
|---|---|
| Android Gradle Plugin (AGP) | 9.4.0 |
| Gradle | 9.6.0 (baixado pelo wrapper) |
| Java (source/target) | 17 |
| compileSdk / targetSdk | 37 (Android 17) |
| minSdk | 26 (Android 8.0) |
| Bibliotecas | `androidx.appcompat:1.7.0`, `com.google.android.material:1.12.0` |

---

## 2. Pré-requisitos

- **Android Studio** (testado com a versão 2026.2 / Rabbit 1 — build `AI-262`).
- **JDK 17+** — o Android Studio já traz o JBR (JetBrains Runtime) embutido; não precisa
  instalar Java separadamente.
- **Android SDK** com:
  - Platform **Android 17 (API 37)** — pasta `platforms/android-37.0`
  - **Build-Tools 36.0.0**
- Um **emulador** ou **aparelho** com Android 8.0+ (API 26+).

No seu computador o SDK está em:

```text
C:\Users\joaod\AppData\Local\Android\Sdk
```

---

## 3. Como executar a interface no Android Studio (passo a passo)

1. Abra o Android Studio.
2. Clique em **Open** e selecione a pasta do projeto:
   `C:\Users\joaod\StudioProjects\appProduto`
3. Aguarde o **Gradle Sync** (barra de progresso na parte de baixo). A primeira sincronização
   baixa o Gradle 9.6.0 e as dependências — pode demorar alguns minutos.
   - Se ele perguntar sobre **trust project / confiar no projeto**, confirme.
   - Se pedir para instalar componentes do SDK que faltam, aceite.
4. Confirme o caminho do SDK em **File → Project Structure → SDK Location**:
   `C:\Users\joaod\AppData\Local\Android\Sdk`
5. Confirme o **Gradle JDK** em **Settings → Build, Execution, Deployment → Build Tools → Gradle**:
   selecione **jbr** (o JDK embutido do Android Studio) ou um JDK 17+.
6. Crie/abra um emulador em **Device Manager** (ícone de celular na barra lateral →
   **Create Device** → escolha um modelo → escolha uma imagem de sistema API 26 ou superior).
7. Selecione o dispositivo no seletor no topo da janela.
8. Clique no botão **▶ Run** (ou `Shift + F10`).
9. **Resultado esperado:** abre a tela “Cadastro de Produto” com os campos **Nome** e
   **Preço**, o botão **SALVAR** e a lista vazia “Produtos cadastrados”. Ao clicar em
   SALVAR aparece o `Toast`: *“Interface pronta! A validação e o SQLite serão
   implementados na próxima etapa.”*

### Se der erro de sync

- **“SDK location not found”** → crie/verifique o arquivo `local.properties` na raiz do
  projeto com o conteúdo:
  ```properties
  sdk.dir=C\:\\Users\\joaod\\AppData\\Local\\Android\\Sdk
  ```
  (esse arquivo é gerado automaticamente pelo Android Studio e é ignorado pelo Git.)
- **“compileSdk 37 not found”** → instale a *Android 17 (API 37)* pelo
  **SDK Manager → SDK Platforms**.
- **“interpreter not found”** → você clicou em *Run* no arquivo `gradlew` (script do
  Linux/Mac). **Não** rode o `gradlew` pelo Android Studio. Use o botão **▶ Run** com a
  configuração **app**, ou, no terminal, use `gradlew.bat` (Windows).
- Qualquer erro de Gradle → **File → Sync Project with Gradle Files**.

### Como rodar o app (configuração “app”)

1. `File → Sync Project with Gradle Files` (cria a configuração **app**).
2. No seletor no topo, escolha **app** e clique em **▶ Run** (`Shift + F10`).
3. Se **app** não aparecer: `Run → Edit Configurations → + → Android App`,
   *Module:* `app` → **OK**. E apague a config antiga **gradlew** (botão **−**).

---

## 4. Como executar pelo terminal (Windows)

Abra o **PowerShell**, **CMD** ou o **Terminal do Android Studio** na pasta do projeto:

```bat
:: Gerar o APK de debug
gradlew.bat assembleDebug

:: Instalar no emulador/aparelho conectado
gradlew.bat installDebug
```

O APK gerado fica em:

```text
app\build\outputs\apk\debug\app-debug.apk
```

Pré-requisito para o terminal: o arquivo `local.properties` precisa existir (normalmente o
Android Studio já cria). O `gradlew.bat` usa o JDK configurado; se reclamar de Java, defina
a variável `JAVA_HOME`:

```bat
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
gradlew.bat assembleDebug
```

---

## 5. SQLite — como “setar” manualmente

### 5.1 Preciso instalar/configurar algo manualmente?

**Não.** O SQLite já faz parte do próprio Android — é uma biblioteca nativa do sistema.
Não se instala banco, não se configura servidor, não se cria arquivo `.db` na mão.

Na **etapa atual (interface)** ainda **não existe banco nenhum**. Quando a classe
`ProdutoDbHelper` (que estende `SQLiteOpenHelper`) for implementada, o arquivo do banco
`produtos.db` será **criado automaticamente** na primeira execução do app, junto com a
tabela `produtos`. Ou seja: o único “set” é escrever o código do `SQLiteOpenHelper` — nada
de configuração externa.

### 5.2 Onde o banco vai ficar

```text
/data/data/com.seuapp.bancodados/databases/produtos.db
```

Essa pasta é **privada do aplicativo**: só dá para acessar via Android Studio (Device
Explorer) ou `adb` em build de debug. Não precisa de permissão no `AndroidManifest.xml`.

### 5.3 Inspecionar o banco pelo Android Studio (Device Explorer)

1. Rode o app pelo menos uma vez (para o banco existir).
2. **View → Tool Windows → Device Explorer** (ou **App Inspection**).
3. Navegue até `data/data/com.seuapp.bancodados/databases/`.
4. Clique com o botão direito em `produtos.db` → **Save As...** para baixar o arquivo.
5. Abra o `.db` no **DB Browser for SQLite** (gratuito) ou use o **Database Inspector**
   do Android Studio (**View → Tool Windows → App Inspection → Database Inspector**),
   que mostra as tabelas em tempo real com o app rodando.

### 5.4 Inspecionar o banco via ADB (terminal)

Com um emulador/aparelho conectado (`adb devices` deve listar o dispositivo):

```bat
:: listar os bancos do app
adb shell run-as com.seuapp.bancodados ls databases/

:: copiar o banco para o PC (no PowerShell use ./ para o caminho local)
adb exec-out run-as com.seuapp.bancodados cat databases/produtos.db > produtos.db
```

Depois abra `produtos.db` num visualizador SQLite. Se tiver o `sqlite3` instalado:

```bat
sqlite3 produtos.db "SELECT * FROM produtos;"
```

> Dicas de consulta (valem quando o banco existir):
> ```sql
> .tables
> .schema produtos
> SELECT * FROM produtos ORDER BY id DESC;
> ```

### 5.5 Como resetar (apagar) os dados

- **No emulador/aparelho:** Configurações → Apps → *Cadastro de Produtos* →
  **Armazenamento → Limpar dados**. Isso apaga o `produtos.db`.
- **Ou desinstale e reinstale o app:** `gradlew.bat uninstallDebug` e depois
  `gradlew.bat installDebug`.
- **Ou, no código**, aumente a versão do banco (`DATABASE_VERSION`) — o `onUpgrade()`
  do plano faz `DROP TABLE` e recria a tabela (apaga tudo).

### 5.6 Resumo do que é manual x automático

| Item | Manual? |
|---|---|
| Instalar o SQLite | Não (já vem no Android) |
| Criar o arquivo `produtos.db` | Automático (o `SQLiteOpenHelper` cria no 1º uso) |
| Criar a tabela `produtos` | Automático (método `onCreate()`) |
| Configurar permissões | Não precisa (banco é privado do app) |
| Definir caminho do SDK (`local.properties`) | Sim, uma vez por máquina (o Android Studio cria sozinho) |
| Ajustar o “Gradle JDK” | Sim, uma vez (usar o **jbr** do Android Studio) |

---

## 6. Estrutura do projeto

```text
appProduto/
├── build.gradle                  # plugins (AGP 9.4.0)
├── settings.gradle               # nome do projeto e módulos
├── gradle.properties             # AndroidX e opções do Gradle
├── gradlew / gradlew.bat         # wrapper do Gradle
├── gradle/wrapper/               # jar + properties do wrapper (Gradle 9.6.0)
├── local.properties              # caminho do SDK (ignorado no Git)
├── .gitignore
├── README.md                     # este arquivo
├── Plano_Persistencia_SQLite_Android.md   # plano da atividade (ignorado no Git)
└── app/
    ├── build.gradle              # módulo app (namespace, SDKs, deps)
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/seuapp/bancodados/
        │   └── MainActivity.java         # interface + clique do botão (provisório)
        └── res/
            ├── layout/activity_main.xml  # formulário + ListView
            ├── values/strings.xml
            ├── values/colors.xml
            ├── values/themes.xml
            ├── drawable/ic_launcher_foreground.xml
            └── mipmap-anydpi-v26/ic_launcher.xml
```

---

## 7. Próximas etapas (conforme o plano)

1. Criar `Produto.java` (modelo: `id`, `nome`, `preco`).
2. Criar `ProdutoDbHelper.java` (estende `SQLiteOpenHelper`; `onCreate`, `onUpgrade`,
   `inserirProduto`, `listarProdutos`).
3. Implementar validação no clique do botão (nome com 3+ caracteres, preço numérico > 0).
4. Inserir no banco e atualizar o `ListView`.
5. Testar persistência fechando e reabrindo o app.

---

## 8. O que está no `.gitignore`

`.gradle/`, `build/`, `local.properties`, `.idea/`, `*.iml`, `out/`, APKs, `.cxx/` e o
arquivo `Plano_Persistencia_SQLite_Android.md`.
