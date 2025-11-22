# FarmaJá - Sistema de Gestão Farmacêutica

Sistema de console em Java para gerenciamento de uma farmácia, incluindo módulos para Clientes, Administradores e Entregadores. O projeto utiliza arquitetura MVC (Model-View-Controller) e banco de dados PostgreSQL para persistência de dados.

## Pré-requisitos

### 1. Java Development Kit (JDK)
* **Java JDK 24** instalado

### 2. PostgreSQL
* **PostgreSQL 18**
* Download: [https://www.enterprisedb.com/downloads/postgres-postgresql-downloads](https://www.enterprisedb.com/downloads/postgres-postgresql-downloads)
* Durante a instalação:
    - Anote a **senha** que você definir para o usuário `postgres`
    - A porta padrão é **5432** (recomendado manter)

---

## Como Executar

### Passo 1: Configurar o Banco de Dados PostgreSQL

#### 1.1 Criar o Banco de Dados

Após instalar o PostgreSQL, abra o **pgAdmin** ou o **psql** (terminal do PostgreSQL) e execute:

```sql
CREATE DATABASE farmaja;
```

#### 1.2 Configurar a Senha no Código

⚠️ **IMPORTANTE**: Se você definiu uma senha diferente de `"senha"` durante a instalação do PostgreSQL, você precisa atualizar o código!

Abra o arquivo:
```
src/main/java/br/com/projeto/farmaja/database/PostgreSQLConnectionFactory.java
```

E altere as linhas 9-11:

```java
// ===== CONFIGURE AQUI SEUS DADOS =====
private static final String DB_URL = "jdbc:postgresql://localhost:5432/farmaja";
private static final String DB_USER = "postgres";  // Seu usuário PostgreSQL
private static final String DB_PASSWORD = "SUA_SENHA_AQUI"; // ⚠️ ALTERE AQUI!
// ====================================
```

**Exemplo:**
- Se sua senha é `admin123`, coloque: `private static final String DB_PASSWORD = "admin123";`
- Se sua porta não é 5432, altere também na URL: `jdbc:postgresql://localhost:PORTA/farmaja`

---

### Passo 2: Importar o Projeto no IntelliJ IDEA

1. **Abra o IntelliJ IDEA**
2. Clique em **File → Open**
3. Navegue até a pasta do projeto `FarmaJa`
4. **IMPORTANTE**: Selecione o arquivo **`pom.xml`** (não a pasta!)
5. Clique em **Open as Project**
6. Aguarde o IntelliJ baixar as dependências do Maven (barra de progresso no canto inferior)

---

### Passo 3: Configurar o JDK no Projeto

1. Vá em **File → Project Structure** (ou pressione `Ctrl+Alt+Shift+S`)
2. Na seção **Project**:
    - **SDK**: Selecione Java 24 (se não tiver, clique em **Add SDK → Download JDK**)
    - **Language Level**: Deixe como "SDK default" ou selecione a mesma versão
3. Clique em **Apply** e **OK**

---

### Passo 4: Validar o Banco de Dados

Antes de executar a aplicação pela primeira vez:

1. Localize o arquivo: `src/main/java/br/com/projeto/farmaja/TestDatabase.java`
2. Clique com o botão direito no arquivo → **Run 'TestDatabase.main()'**
3. Verifique no console se aparece:
   ```
   Conexão com PostgreSQL estabelecida.
   Banco de dados PostgreSQL inicializado com sucesso!
   ✓ Tabela 'usuarios' existe (registros: 1)
   ✓ Tabela 'enderecos' existe...
   ...
   === TODOS OS TESTES PASSARAM COM SUCESSO! ===
   ```

**Se der erro de conexão:**
- Confirme que a senha em `PostgreSQLConnectionFactory.java` está correta
- Verifique se o banco `farmaja` foi criado

---

### Passo 5: Executar a Aplicação

1. Localize o arquivo principal: `src/main/java/br/com/projeto/farmaja/Aplicacao.java`
2. Clique com o botão direito → **Run 'Aplicacao.main()'**
3. O sistema iniciará no console mostrando o **Menu Principal**

---

## Estrutura de Pacotes

O projeto está organizado seguindo o padrão MVC:

* **`br.com.projeto.farmaja`**: Pacote raiz
    * `Aplicacao.java`: Ponto de entrada (Main) do sistema
* **`.model`**: Classes que representam os dados (Ex: `Medicamento`, `Usuario`, `Pedido`, `Favorito`)
* **`.view`**: Telas e Menus do console
    * `MenuPrincipal.java`: Roteador inicial
    * `LoginView.java`: Lógica de autenticação
    * `/Cliente`, `/Admin`, `/Entregador`: Menus específicos de cada perfil
* **`.controller`**: Regras de negócio e ponte entre View e DAO
* **`.dao`**: Acesso a dados (comunicação com PostgreSQL)
* **`.database`**: Configuração de conexão com o banco de dados
* **`.util`**: Ferramentas auxiliares (Ex: `LeitorConsole.java` para leitura segura)

---

## Credenciais de Acesso

O sistema cria automaticamente um usuário administrador padrão:

### Administrador
- **Email:** `admin@farmaja.com`
- **Senha:** `admin123`

**Funcionalidades:**
- Realizar vendas
- Gerenciar pedidos e status de entrega
- Cadastrar medicamentos e controlar estoque
- Gerenciar usuários (Clientes, Entregadores, Administradores)
- Gerenciar fornecedores
- Visualizar relatórios de estoque baixo

### Cliente (Criar no Sistema)
Após logar como Admin, vá em **"4. Gestão de Usuários" → "1. Cadastrar Novo Usuário"** e crie um cliente com tipo `CLIENTE`.

**Funcionalidades:**
- Ver catálogo de medicamentos
- Buscar produtos por nome ou código
- Adicionar itens ao carrinho
- Finalizar compras com múltiplas formas de pagamento
- Acompanhar status de pedidos
- **Gerenciar favoritos** (medicamentos)
- Editar perfil e cadastrar múltiplos endereços

### Entregador (Criar no Sistema)
Crie um usuário com tipo `ENTREGADOR`.

**Funcionalidades:**
- Ver entregas pendentes (pedidos em transporte)
- Visualizar endereço de entrega e dados do cliente
- Marcar entregas como concluídas

---

## Exemplos de Uso (Fluxos Principais)

### 1. Primeiro Acesso - Configurar o Sistema

1. **Execute `TestDatabase.java`** (valida as tabelas e o admin)
2. **Execute `Aplicacao.java`** (inicia o sistema)
3. **Faça login como Admin** (opção 2)
    - Email: `admin@farmaja.com`
    - Senha: `admin123`
4. **Cadastre um Fornecedor** (menu 5 → opção 1)
5. **Cadastre Medicamentos** (menu 3 → opção 1)
6. **Cadastre um Cliente** (menu 4 → opção 1, tipo: `CLIENTE`)
7. **Cadastre um Entregador** (menu 4 → opção 1, tipo: `ENTREGADOR`)

---

### 2. Fluxo de Compra como Cliente

1. **Faça login como Cliente** (opção 1 no menu principal)
2. **Ver Catálogo** (opção 1) ou **Buscar Medicamento** (opção 2)
3. **Adicionar ao Carrinho** (opção 3):
    - Digite o ID do medicamento
    - Informe a quantidade desejada
4. **Ver Carrinho / Finalizar Pedido** (opção 4):
    - Escolha o endereço de entrega
    - Selecione a forma de pagamento (PIX, CREDITO, DEBITO, DINHEIRO)
    - Adicione observações (opcional)
5. **Acompanhar Pedidos** (opção 5) para ver o status

---

### 3. Gerenciar Favoritos

1. **Entre no Menu de Favoritos** (opção 7)
2. **Adicionar Medicamento aos Favoritos** (opção 3)
    - Visualize o catálogo
    - Digite o ID do medicamento
3. **Ver Medicamentos Favoritos** (opção 1)
    - Lista todos os seus favoritos
    - Opção de adicionar direto ao carrinho
4. **Remover dos Favoritos** (opção 4)

---

### 4. Gestão de Entregas (Entregador)

1. **Faça login como Entregador** (opção 3)
2. **Ver Minhas Entregas** (opção 1)
    - Lista pedidos atribuídos a você
    - Mostra endereço completo do cliente
3. **Marcar como Concluída** (opção 2)
    - Digite o ID do pedido entregue
    - Sistema atualiza o status para "ENTREGUE"

---

## Funcionalidades Técnicas

### Segurança e Validação
* **Autenticação por email/senha** armazenada no banco
* **Validação de tipo de usuário** (Cliente não acessa menu Admin)
* **Verificação de usuário ativo** antes do login
* **Controle de estoque** em tempo real
* **Validação de dados** em todas as entradas

### Favoritos
* **Adicionar medicamentos aos favoritos** para acesso rápido
* **Visualizar lista de favoritos** organizada
* **Remover favoritos** facilmente
* **Adicionar ao carrinho** direto dos favoritos
* **Prevenção de duplicatas** automática

### Gestão de Pedidos
* **Estados do pedido:** PENDENTE → CONFIRMADO → PREPARANDO → PRONTO_PARA_ENTREGA → EM_TRANSPORTE → ENTREGUE / CANCELADO
* **Histórico completo** de mudanças de status
* **Atribuição de entregadores** pelo admin
* **Rastreamento em tempo real** pelos clientes

### Controle de Estoque
* **Atualização automática** ao realizar vendas
* **Alertas de estoque baixo** (quando atinge estoque mínimo)
* **Entrada e saída de produtos** com registro
* **Validação de disponibilidade** antes de finalizar compra

---

## Estrutura do Banco de Dados

### Tabelas Principais:
- **usuarios**: Clientes, Administradores e Entregadores
- **enderecos**: Endereços de entrega dos usuários
- **fornecedores**: Fornecedores de medicamentos
- **medicamentos**: Catálogo de produtos
- **pedidos**: Cabeçalho dos pedidos (header)
- **itens_pedido**: Itens de cada pedido (linhas)
- **historico_entregas**: Log de mudanças de status
- **favoritos**: Medicamentos e fornecedores favoritos dos usuários

---

Trabalho desenvolvido para a disciplina de Programação Orientada a Objetos.
