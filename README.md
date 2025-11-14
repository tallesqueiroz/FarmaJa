# FarmaJá - Sistema de Gestão Farmacêutica

Sistema de console em Java para gerenciamento de uma farmácia, incluindo módulos para Clientes, Administradores e Entregadores. O projeto utiliza arquitetura MVC (Model-View-Controller) e persistência de dados em memória (ou banco de dados, dependendo da configuração DAO).

## 🚀 Como Executar

### Pré-requisitos
* Java JDK 17 ou superior instalado.
* Uma IDE (IntelliJ, Eclipse, VSCode) ou terminal.

### Passo a Passo
1.  Localize a classe principal: `src/main/java/br/com/projeto/farmaja/Aplicacao.java`.
2.  Execute o arquivo `Aplicacao.java`.
3.  O sistema iniciará exibindo o **Menu Principal**.

---

## 📂 Estrutura de Pacotes

O projeto está organizado seguindo o padrão MVC:

* **`br.com.projeto.farmaja`**: Pacote raiz.
    * `Aplicacao.java`: Ponto de entrada (Main) do sistema.
* **`.model`**: Classes que representam os dados (Ex: `Medicamento`, `Usuario`, `Pedido`).
* **`.view`**: Telas e Menus do console.
    * `MenuPrincipal.java`: Roteador inicial.
    * `LoginView.java`: Lógica de autenticação.
    * `/Cliente`, `/Admin`, `/Entregador`: Menus específicos de cada perfil.
* **`.controller`**: Regras de negócio e ponte entre View e DAO.
* **`.dao`**: Acesso a dados (simulação de banco de dados).
* **`.util`**: Ferramentas auxiliares (Ex: `LeitorConsole.java` para leitura segura de dados).

---

## Exemplos de Uso (Fluxos Principais)

### 1. Acesso Administrativo (SuperAdmin)
* **Login:** Selecione a opção `2` no menu principal.
    * *Email:* `admin@farmaja.com`
    * *Senha:* `admin123`
* **Funcionalidades:**
    * Cadastrar novos medicamentos (com controle de estoque).
    * Gerenciar usuários e fornecedores.
    * Ver relatórios de estoque baixo.

### 2. Acesso do Cliente (Realizar Compra)
* **Login:** Selecione a opção `1` no menu principal.
    * *Email:* `cliente@farmaja.com`
    * *Senha:* `cliente123`
* **Fluxo de Compra:**
    1.  Acesse "1. Ver Catálogo" ou "2. Buscar Medicamento".
    2.  Utilize "3. Adicionar ao Carrinho" (Informe ID do produto e quantidade).
    3.  Vá em "4. Ver Carrinho / Finalizar Pedido" e escolha a opção de checkout.
    4.  Selecione o endereço e a forma de pagamento.
    5.  O status do pedido inicia como `PENDENTE`.

### 3. Acesso do Entregador
* **Login:** Selecione a opção `3` no menu principal.
    * *Email:* `entregador@farmaja.com`
    * *Senha:* `entregador123`
* **Fluxo:**
    1.  Visualize entregas pendentes com endereço e nome do cliente.
    2.  Marque entregas como `CONCLUÍDA` utilizando o ID do pedido.

---

## 🛠️ Funcionalidades Técnicas

* **Leitura Segura:** O sistema trata exceções (`try-catch`) para evitar falhas caso o usuário digite texto em campos numéricos.
* **Confirmação de Exclusão:** Ações críticas (como limpar carrinho ou deletar fornecedores) exigem confirmação (S/N).
* **Busca Inteligente:** A busca de medicamentos funciona por código exato ou por partes do nome (case-insensitive).

---

## 👨‍💻 Autores
Trabalho desenvolvido para a disciplina de Programação Orientada a Objetos.