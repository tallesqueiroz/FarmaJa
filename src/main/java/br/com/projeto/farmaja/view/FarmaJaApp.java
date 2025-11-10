package br.com.projeto.farmaja.view;

import br.com.projeto.farmaja.controller.FornecedorController;
import br.com.projeto.farmaja.controller.MedicamentoController;
import br.com.projeto.farmaja.controller.PedidoController;
import br.com.projeto.farmaja.controller.UsuarioController;
import br.com.projeto.farmaja.dao.*;
import br.com.projeto.farmaja.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FarmaJaApp {

    // --- Camada DAO ---
    private static final MedicamentoDAO medicamentoDAO = new MedicamentoDAO();
    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static final EnderecoDAO enderecoDAO = new EnderecoDAO();
    private static final FornecedorDAO fornecedorDAO = new FornecedorDAO();
    private static final PedidoDAO pedidoDAO = new PedidoDAO();
    private static final ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO();
    private static final HistoricoEntregaDAO historicoEntregaDAO = new HistoricoEntregaDAO();

    // --- Camada Controller ---
    private static final MedicamentoController medicamentoController = new MedicamentoController(medicamentoDAO);
    private static final UsuarioController usuarioController = new UsuarioController(usuarioDAO, enderecoDAO);
    private static final FornecedorController fornecedorController = new FornecedorController(fornecedorDAO);
    private static final PedidoController pedidoController = new PedidoController(
            pedidoDAO, itemPedidoDAO, medicamentoDAO, usuarioDAO, historicoEntregaDAO
    );

    // --- Ferramentas da View ---
    private static final Scanner scanner = new Scanner(System.in);
    private static Usuario usuarioLogado = null; // Usuário logado

    public static void main(String[] args) {
        // Simulação de login para testes
        simularLoginAdmin();
        System.out.println("Bem-vindo ao FarmaJá, " + usuarioLogado.getNome());

        // Loop principal do sistema
        while (true) {
            exibirMenuPrincipal();
            int opcao = lerOpcaoInt();

            switch (opcao) {
                case 1:
                    menuRealizarVenda();
                    break;
                case 2:
                    menuGestaoPedidos();
                    break;
                case 3:
                    menuGestaoMedicamentos();
                    break;
                case 4:
                    menuGestaoUsuarios();
                    break;
                case 5:
                    menuGestaoFornecedores();
                    break;
                case 6:
                    menuRelatorios();
                    break;
                case 0:
                    System.out.println("Saindo do sistema. Até logo!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
            pressionarEnterParaContinuar();
        }
    }

    private static void simularLoginAdmin() {
        // Tenta buscar o admin@farmaja.com, se não existir, cria.
        usuarioLogado = usuarioController.login("admin@farmaja.com", "admin123");
        if (usuarioLogado == null) {
            System.out.println("Criando usuário ADMIN padrão...");
            try {
                // Usa o construtor do seu model Usuario
                Usuario admin = new Usuario(
                        "Admin Padrão",
                        "admin@farmaja.com",
                        "admin123", // Em sistema real, usar HASH
                        "00000000000",
                        "999999999",
                        "ADMINISTRADOR" // Seu model valida "ADMINISTRADOR"
                );

                // Usa o construtor do seu model Endereco (passando 0 como placeholder de ID)
                // O controller irá atualizar o ID do usuário
                Endereco end = new Endereco();
                end.setRua("Rua da Matriz");
                end.setNumero("123");
                end.setBairro("Centro");
                end.setCidade("São Paulo");
                end.setEstado("SP");
                end.setCep("01000-000");

                // O controller cadastra usuário e endereço
                usuarioController.cadastrarNovoCliente(admin, end);
                usuarioLogado = admin;
            } catch (IllegalArgumentException e) {
                System.out.println("Erro crítico ao criar admin padrão: " + e.getMessage());
                System.exit(1); // Sai se não puder criar o admin
            }
        }
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. REALIZAR VENDA");
        System.out.println("2. Gestão de Pedidos");
        System.out.println("3. Gestão de Medicamentos");
        System.out.println("4. Gestão de Usuários");
        System.out.println("5. Gestão de Fornecedores");
        System.out.println("6. Relatórios");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    // ===================================================================
    // 1. FLUXO DE VENDA
    // ===================================================================
    private static void menuRealizarVenda() {
        System.out.println("\n--- 💵 Nova Venda ---");
        try {
            // 1. Selecionar Cliente
            Usuario cliente = selecionarUsuarioPorTipo("CLIENTE");
            if (cliente == null) return;

            // 2. Selecionar Endereço
            Endereco endereco = selecionarEndereco(cliente.getId());
            if (endereco == null) return;

            // 3. Adicionar Itens ao Carrinho
            List<ItemPedido> carrinho = new ArrayList<>();
            while (true) {
                System.out.print("\nDigite o código do medicamento (ou '0' para finalizar): ");
                String codigo = lerString();
                if (codigo.equals("0")) break;

                Medicamento med = medicamentoController.buscarPorCodigo(codigo);

                // Usa getAtivo() (Boolean) e checagem segura
                if (med == null || !Boolean.TRUE.equals(med.getAtivo())) {
                    System.out.println("Medicamento não encontrado ou inativo.");
                    continue;
                }

                System.out.println("Medicamento: " + med.getNome());
                System.out.println("Estoque: " + med.getEstoque() + " | Preço: R$" + med.getPreco());
                System.out.print("Quantidade: ");
                int qtd = lerOpcaoInt();

                if (qtd <= 0) {
                    System.out.println("Quantidade inválida.");
                    continue;
                }
                if (qtd > med.getEstoque()) {
                    System.out.println("Estoque insuficiente. (Disponível: " + med.getEstoque() + ")");
                    continue;
                }

                // Adiciona ao carrinho
                // Usa o construtor do seu model ItemPedido
                // O preço unitário será setado no controller
                ItemPedido item = new ItemPedido(
                        med.getId(),
                        qtd,
                        med.getPreco() // Passa o preço aqui, o model já calcula o subtotal
                );
                carrinho.add(item);
                System.out.println(qtd + "x " + med.getNome() + " adicionado(s).");
            }

            if (carrinho.isEmpty()) {
                System.out.println("Venda cancelada (sem itens).");
                return;
            }

            // 4. Forma de Pagamento
            System.out.print("Forma de Pagamento (PIX, CREDITO, DINHEIRO): ");
            String formaPgto = lerString();

            // 5. Observações
            System.out.print("Observações (opcional): ");
            String obs = lerString();

            // 6. Montar o Pedido
            // Usa o construtor do seu model Pedido
            Pedido novoPedido = new Pedido(
                    cliente.getId(),
                    endereco.getId(),
                    formaPgto
            );
            novoPedido.setObservacoes(obs);

            // 7. Chama o Controller para orquestrar a criação
            String resultado = pedidoController.criarNovoPedido(novoPedido, carrinho);

            System.out.println("\n" + resultado);

        } catch (IllegalArgumentException e) {
            System.out.println("Erro de validação: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro fatal ao realizar venda: " + e.getMessage());
        }
    }

    private static Usuario selecionarUsuarioPorTipo(String tipoUsuario) {
        List<Usuario> usuarios = usuarioController.listarUsuariosPorTipo(tipoUsuario);
        if (usuarios == null || usuarios.isEmpty()) {
            System.out.println("Nenhum usuário do tipo '" + tipoUsuario + "' encontrado.");
            return null;
        }

        System.out.println("\n--- Selecione o " + tipoUsuario + " ---");
        for (Usuario u : usuarios) {
            System.out.printf("ID: %d | Nome: %s | CPF: %s\n", u.getId(), u.getNome(), u.getCpf());
        }

        while(true) {
            System.out.print("Digite o ID do " + tipoUsuario + ": ");
            int id = lerOpcaoInt();
            for (Usuario u : usuarios) {
                if (u.getId() == id) return u;
            }
            System.out.println("ID inválido.");
        }
    }

    private static Endereco selecionarEndereco(int usuarioId) {
        List<Endereco> enderecos = usuarioController.listarEnderecosPorUsuario(usuarioId);
        if (enderecos == null || enderecos.isEmpty()) {
            System.out.println("Usuário não possui endereços cadastrados. Cancele e cadastre um endereço.");
            return null;
        }

        System.out.println("\n--- Selecione o Endereço de Entrega ---");
        int count = 1;
        for (Endereco e : enderecos) {
            System.out.printf("%d. %s, %s - %s\n", count++, e.getRua(), e.getNumero(), e.getBairro());
        }

        while(true) {
            System.out.print("Digite o número do endereço (1, 2...): ");
            int op = lerOpcaoInt();
            if (op > 0 && op <= enderecos.size()) {
                return enderecos.get(op - 1);
            }
            System.out.println("Opção inválida.");
        }
    }

    // ===================================================================
    // 2. GESTÃO DE PEDIDOS
    // ===================================================================
    private static void menuGestaoPedidos() {
        System.out.println("\n--- 🚚 Gestão de Pedidos ---");
        // Ajusta texto para bater com o status "PENDENTE"
        System.out.println("1. Listar Pedidos Pendentes (Aguardando Pagamento)");
        System.out.println("2. Listar Pedidos Prontos para Entrega");
        System.out.println("3. Atribuir Entregador (Mover para 'Em Transporte')");
        System.out.println("4. Marcar Pedido como 'Entregue'");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");

        int op = lerOpcaoInt();
        switch(op) {
            case 1:
                listarPedidosPorStatus("PENDENTE");
                break;
            case 2:
                // O DAO busca por "PRONTO_PARA_ENTREGA", o que está correto
                listarPedidosPendentesAtribuicao();
                break;
            case 3:
                atribuirEntregador();
                break;
            case 4:
                marcarPedidoEntregue();
                break;
            case 0: break;
            default: System.out.println("Opção inválida.");
        }
    }

    private static void listarPedidosPorStatus(String status) {
        System.out.println("\n--- Pedidos com Status: " + status + " ---");
        List<Pedido> pedidos = pedidoController.listarPedidosPorStatus(status);
        if (pedidos == null || pedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
            return;
        }
        for (Pedido p : pedidos) {
            System.out.printf("ID: %d | Data: %s | ClienteID: %d | Valor: R$%.2f\n",
                    p.getId(), p.getDataPedido(), p.getClienteId(), p.getValorTotal());
        }
    }

    private static void listarPedidosPendentesAtribuicao() {
        System.out.println("\n--- Pedidos Prontos para Entrega ---");
        List<Pedido> pedidos = pedidoController.listarPedidosPendentesAtribuicao();
        if (pedidos == null || pedidos.isEmpty()) {
            System.out.println("Nenhum pedido pendente.");
            return;
        }
        for (Pedido p : pedidos) {
            System.out.printf("ID: %d | Data: %s | ClienteID: %d | Valor: R$%.2f\n",
                    p.getId(), p.getDataPedido(), p.getClienteId(), p.getValorTotal());
        }
    }

    private static void atribuirEntregador() {
        System.out.println("\n--- Atribuir Entregador ---");
        listarPedidosPendentesAtribuicao();
        System.out.print("Digite o ID do Pedido: ");
        int pedidoId = lerOpcaoInt();
        if (pedidoId <= 0) return;

        Usuario entregador = selecionarUsuarioPorTipo("ENTREGADOR");
        if (entregador == null) {
            System.out.println("Atribuição cancelada.");
            return;
        }

        // O status válido no seu model é "EM_TRANSPORTE"
        String res = pedidoController.atualizarStatusPedido(
                pedidoId,
                "EM_TRANSPORTE",
                "Atribuído ao entregador: " + entregador.getNome(),
                entregador.getId()
        );
        System.out.println(res);
    }

    private static void marcarPedidoEntregue() {
        System.out.println("\n--- Marcar Pedido como Entregue ---");
        // Lista os pedidos que estão "EM_TRANSPORTE"
        listarPedidosPorStatus("EM_TRANSPORTE");
        System.out.print("Digite o ID do Pedido que foi entregue: ");
        int pedidoId = lerOpcaoInt();

        if (pedidoId <= 0) return;

        String res = pedidoController.atualizarStatusPedido(
                pedidoId,
                "ENTREGUE",
                "Pedido marcado como entregue pelo sistema.",
                null // Entregador já estava atribuído
        );
        System.out.println(res);
    }

    // ===================================================================
    // 3. GESTÃO DE MEDICAMENTOS
    // ===================================================================
    private static void menuGestaoMedicamentos() {
        System.out.println("\n--- 💊 Gestão de Medicamentos ---");
        System.out.println("1. Cadastrar Novo Medicamento");
        System.out.println("2. Listar Medicamentos Ativos");
        System.out.println("3. Buscar por Código");
        System.out.println("4. Atualizar Estoque");
        System.out.println("5. Ativar/Desativar Medicamento");
        System.out.println("0. Voltar ao Menu Principal");
        System.out.print("Escolha uma opção: ");

        int opcao = lerOpcaoInt();

        switch (opcao) {
            case 1:
                cadastrarMedicamento();
                break;
            case 2:
                listarMedicamentosAtivos();
                break;
            case 3:
                buscarMedicamentoPorCodigo();
                break;
            case 4:
                atualizarEstoque();
                break;
            case 5:
                ativarDesativarMedicamento();
                break;
            case 0:
                System.out.println("Voltando...");
                break;
            default:
                System.out.println("Opção inválida.");
        }
    }

    private static void cadastrarMedicamento() {
        try {
            System.out.println("\n--- Cadastro de Medicamento ---");
            System.out.print("Código (ex: 789...): ");
            String codigo = lerString();
            System.out.print("Nome: ");
            String nome = lerString();
            System.out.print("Descrição: ");
            String desc = lerString();
            System.out.print("Preço (ex: 19.99): ");
            BigDecimal preco = lerOpcaoBigDecimal();
            System.out.print("Estoque Inicial: ");
            int estoque = lerOpcaoInt();
            System.out.print("Estoque Mínimo: ");
            int estoqueMin = lerOpcaoInt();
            System.out.print("Requer Receita? (s/n): ");
            boolean receita = lerString().equalsIgnoreCase("s");

            // TODO: Selecionar Fornecedor da lista
            Integer fornecedorId = 1; // Simulado
            System.out.println("Usando Fornecedor ID (Simulado): " + fornecedorId);

            // Usa o construtor do seu model Medicamento
            // O construtor já define 'ativo = true' e 'dataCriacao'
            Medicamento med = new Medicamento(
                    codigo, nome, desc, preco, estoque, estoqueMin,
                    fornecedorId, receita
            );

            String resultado = medicamentoController.cadastrarMedicamento(med);
            System.out.println(resultado);

        } catch (IllegalArgumentException e) {
            // Captura erros de validação dos setters/construtor do model
            System.out.println("Erro de validação: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
        }
    }

    private static void listarMedicamentosAtivos() {
        System.out.println("\n--- Medicamentos Ativos ---");
        List<Medicamento> medicamentos = medicamentoController.listarMedicamentosAtivos();
        if (medicamentos == null || medicamentos.isEmpty()) {
            System.out.println("Nenhum medicamento ativo encontrado.");
            return;
        }
        System.out.printf("%-5s | %-12s | %-20s | %-8s | %-5s\n",
                "ID", "Código", "Nome", "Preço", "Est.");
        System.out.println("-----------------------------------------------------------------");
        for (Medicamento med : medicamentos) {
            System.out.printf("%-5d | %-12s | %-20s | R$%-7.2f | %-5d\n",
                    med.getId(), med.getCodigo(), med.getNome(), med.getPreco(), med.getEstoque());
        }
    }

    private static void buscarMedicamentoPorCodigo() {
        System.out.println("\n--- Buscar Medicamento por Código ---");
        System.out.print("Digite o código: ");
        String codigo = lerString();
        Medicamento med = medicamentoController.buscarPorCodigo(codigo);

        if (med != null) {
            System.out.println("Medicamento Encontrado:");
            System.out.println("ID: " + med.getId());
            System.out.println("Nome: " + med.getNome());
            System.out.println("Estoque: " + med.getEstoque());
            // Usa getAtivo() (Boolean) e checagem segura
            System.out.println("Status: " + (Boolean.TRUE.equals(med.getAtivo()) ? "Ativo" : "Inativo"));
        } else {
            System.out.println("Nenhum medicamento encontrado com o código: " + codigo);
        }
    }

    private static void atualizarEstoque() {
        System.out.println("\n--- Atualizar Estoque ---");
        System.out.print("Digite o ID ou Código do medicamento: ");
        String busca = lerString();

        Medicamento med = medicamentoController.buscarPorCodigo(busca);
        if (med == null) {
            try { med = medicamentoDAO.buscarPorId(Integer.parseInt(busca)); }
            catch (Exception e) { /* ignora */ }
        }

        if (med == null) {
            System.out.println("Medicamento não encontrado.");
            return;
        }

        System.out.println("Medicamento: " + med.getNome());
        System.out.println("Estoque Atual: " + med.getEstoque());
        System.out.print("Quantidade a adicionar (use negativo para remover): ");
        int qtd = lerOpcaoInt();

        String res = medicamentoController.atualizarEstoque(med.getId(), qtd);
        System.out.println(res);
    }

    private static void ativarDesativarMedicamento() {
        System.out.print("Digite o ID do medicamento para ativar/desativar: ");
        int id = lerOpcaoInt();
        Medicamento med = medicamentoDAO.buscarPorId(id);
        if (med == null) {
            System.out.println("Medicamento não encontrado.");
            return;
        }

        // Usa getAtivo() (Boolean) e checagem segura para inverter
        boolean novoStatus = !Boolean.TRUE.equals(med.getAtivo());

        String res = medicamentoController.ativarDesativar(id, novoStatus);
        System.out.println(res);
    }

    // ===================================================================
    // 4. GESTÃO DE USUÁRIOS
    // ===================================================================
    private static void menuGestaoUsuarios() {
        System.out.println("\n--- 👤 Gestão de Usuários ---");
        // Ajusta texto para "ADMINISTRADOR"
        System.out.println("1. Cadastrar Novo Usuário (CLIENTE, ENTREGADOR, ADMINISTRADOR)");
        System.out.println("2. Listar Usuários por Tipo");
        System.out.println("3. Adicionar Endereço a um Usuário");
        System.out.println("4. Listar Endereços de um Usuário");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");

        int op = lerOpcaoInt();
        switch(op) {
            case 1:
                cadastrarNovoUsuario();
                break;
            case 2:
                listarUsuariosPorTipo();
                break;
            case 3:
                adicionarEnderecoAUsuario();
                break;
            case 4:
                listarEnderecosDeUsuario();
                break;
            case 0: break;
            default: System.out.println("Opção inválida.");
        }
    }

    private static void cadastrarNovoUsuario() {
        try {
            System.out.println("\n--- Cadastro de Novo Usuário ---");
            System.out.print("Nome: ");
            String nome = lerString();
            System.out.print("Email: ");
            String email = lerString();
            System.out.print("Senha: ");
            String senha = lerString();
            System.out.print("CPF (só números): ");
            String cpf = lerString();
            System.out.print("Telefone: ");
            String tel = lerString();
            System.out.print("Tipo (CLIENTE, ENTREGADOR, ADMINISTRADOR): ");
            String tipo = lerString().toUpperCase();

            // Usa o construtor do seu model Usuario
            Usuario user = new Usuario(nome, email, senha, cpf, tel, tipo);

            System.out.println("--- Endereço Principal ---");
            System.out.print("Rua: ");
            String rua = lerString();
            System.out.print("Número: ");
            String num = lerString();
            System.out.print("Bairro: ");
            String bairro = lerString();
            System.out.print("Cidade: ");
            String cidade = lerString();
            System.out.print("Estado (UF): ");
            String uf = lerString();
            System.out.print("CEP: ");
            String cep = lerString();
            System.out.print("Complemento (opcional): ");
            String comp = lerString();

            // Usa o construtor padrão e os setters (que têm validação)
            // O usuarioId será setado dentro do controller
            Endereco end = new Endereco();
            end.setRua(rua);
            end.setNumero(num);
            end.setBairro(bairro);
            end.setCidade(cidade);
            end.setEstado(uf);
            end.setCep(cep);
            end.setComplemento(comp);

            // Controller orquestra a criação dos dois
            String res = usuarioController.cadastrarNovoCliente(user, end);
            System.out.println(res);

        } catch (IllegalArgumentException e) {
            // Captura erros de validação dos models
            System.out.println("Erro de validação: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        }
    }

    private static void listarUsuariosPorTipo() {
        System.out.print("Digite o tipo (CLIENTE, ENTREGADOR, ADMINISTRADOR): ");
        String tipo = lerString().toUpperCase();

        List<Usuario> usuarios = usuarioController.listarUsuariosPorTipo(tipo);
        if (usuarios == null || usuarios.isEmpty()) {
            System.out.println("Nenhum usuário encontrado para o tipo: " + tipo);
            return;
        }

        System.out.println("\n--- Usuários do Tipo: " + tipo + " ---");
        for (Usuario u : usuarios) {
            // --- CORREÇÃO APLICADA AQUI ---
            // Usa getAtivo() (Boolean) e não isAtivo() (boolean)
            System.out.printf("ID: %d | Nome: %s | Email: %s | Ativo: %b\n",
                    u.getId(), u.getNome(), u.getEmail(), u.getAtivo());
        }
    }

    private static void adicionarEnderecoAUsuario() {
        try {
            System.out.print("Digite o ID do usuário para adicionar endereço: ");
            int userId = lerOpcaoInt();

            Usuario u = usuarioController.buscarUsuarioPorId(userId);
            if (u == null) {
                System.out.println("Usuário não encontrado.");
                return;
            }

            System.out.println("Adicionando endereço para: " + u.getNome());
            System.out.print("Rua: ");
            String rua = lerString();
            System.out.print("Número: ");
            String num = lerString();
            System.out.print("Bairro: ");
            String bairro = lerString();
            System.out.print("Cidade: ");
            String cidade = lerString();
            System.out.print("Estado (UF): ");
            String uf = lerString();
            System.out.print("CEP: ");
            String cep = lerString();
            System.out.print("Complemento (opcional): ");
            String comp = lerString();

            // Usa o construtor do seu model Endereco
            Endereco end = new Endereco(userId, rua, num, bairro, cidade, uf, cep, comp);

            String res = usuarioController.adicionarEndereco(end);
            System.out.println(res);

        } catch (IllegalArgumentException e) {
            System.out.println("Erro de validação: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao adicionar endereço: " + e.getMessage());
        }
    }

    private static void listarEnderecosDeUsuario() {
        System.out.print("Digite o ID do usuário: ");
        int userId = lerOpcaoInt();
        if (userId <= 0) return;

        List<Endereco> enderecos = usuarioController.listarEnderecosPorUsuario(userId);
        if (enderecos == null || enderecos.isEmpty()) {
            System.out.println("Nenhum endereço encontrado para este usuário.");
            return;
        }

        System.out.println("\n--- Endereços de (ID: " + userId + ") ---");
        for(Endereco e : enderecos) {
            // Usa o toString() do seu model Endereco
            System.out.println(e.toString());
        }
    }

    // ===================================================================
    // 5. GESTÃO DE FORNECEDORES
    // ===================================================================
    private static void menuGestaoFornecedores() {
        System.out.println("\n--- 🏭 Gestão de Fornecedores ---");
        System.out.println("1. Cadastrar Novo Fornecedor");
        System.out.println("2. Listar Fornecedores Ativos");
        System.out.println("3. Buscar por CNPJ");
        System.out.println("4. Excluir Fornecedor");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");

        int op = lerOpcaoInt();
        switch(op) {
            case 1:
                cadastrarFornecedor();
                break;
            case 2:
                listarFornecedoresAtivos();
                break;
            case 3:
                buscarFornecedorPorCnpj();
                break;
            case 4:
                deletarFornecedor();
                break;
            case 0: break;
            default: System.out.println("Opção inválida.");
        }
    }

    private static void cadastrarFornecedor() {
        try {
            System.out.println("\n--- Cadastro de Fornecedor ---");
            System.out.print("Nome/Razão Social: ");
            String nome = lerString();
            System.out.print("CNPJ (só números): ");
            String cnpj = lerString();
            System.out.print("Telefone: ");
            String tel = lerString();
            System.out.print("Email: ");
            String email = lerString();

            // Usa o construtor do seu model Fornecedor
            Fornecedor f = new Fornecedor(nome, cnpj, tel, email);

            // O construtor já seta ativo=true

            String res = fornecedorController.cadastrar(f);
            System.out.println(res);

        } catch (IllegalArgumentException e) {
            System.out.println("Erro de validação: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        }
    }

    private static void listarFornecedoresAtivos() {
        System.out.println("\n--- Fornecedores Ativos ---");
        List<Fornecedor> fornecedores = fornecedorController.listarFornecedoresAtivos();
        if (fornecedores == null || fornecedores.isEmpty()) {
            System.out.println("Nenhum fornecedor ativo.");
            return;
        }
        for (Fornecedor f : fornecedores) {
            System.out.printf("ID: %d | Nome: %s | CNPJ: %s | Email: %s\n",
                    f.getId(), f.getNome(), f.getCnpj(), f.getEmail());
        }
    }

    private static void buscarFornecedorPorCnpj() {
        System.out.print("Digite o CNPJ (só números): ");
        String cnpj = lerString();
        Fornecedor f = fornecedorController.buscarPorCnpj(cnpj);
        if (f == null) {
            System.out.println("Nenhum fornecedor encontrado com este CNPJ.");
            return;
        }
        System.out.println("--- Fornecedor Encontrado ---");
        System.out.println("ID: " + f.getId());
        System.out.println("Nome: " + f.getNome());
        System.out.println("Telefone: " + f.getTelefone());
        // Usa getAtivo()
        System.out.println("Status: " + (Boolean.TRUE.equals(f.getAtivo()) ? "Ativo" : "Inativo"));
    }

    private static void deletarFornecedor() {
        System.out.print("Digite o ID do fornecedor a DELETAR: ");
        int id = lerOpcaoInt();
        if (id <= 0) return;

        System.out.print("Tem certeza que deseja excluir o ID " + id + "? (s/n): ");
        if (lerString().equalsIgnoreCase("s")) {
            String res = fornecedorController.deletar(id);
            System.out.println(res);
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }

    // ===================================================================
    // 6. RELATÓRIOS
    // ===================================================================
    private static void menuRelatorios() {
        System.out.println("\n--- 📊 Relatórios ---");
        System.out.println("1. Medicamentos com Estoque Baixo");
        System.out.println("0. Voltar");

        int opcao = lerOpcaoInt();
        if (opcao == 1) {
            listarEstoqueBaixo();
        }
    }

    private static void listarEstoqueBaixo() {
        System.out.println("\n--- Relatório: Estoque Baixo ---");
        List<Medicamento> medicamentos = medicamentoController.listarEstoqueBaixo();
        if (medicamentos == null || medicamentos.isEmpty()) {
            System.out.println("Nenhum medicamento com estoque baixo.");
            return;
        }
        System.out.printf("%-5s | %-20s | %-8s | %-8s\n",
                "ID", "Nome", "Est. Atual", "Est. Mín.");
        System.out.println("----------------------------------------------------");
        for (Medicamento med : medicamentos) {
            // Usa o método de apoio do seu model Medicamento
            if (med.estoqueBaixo()) {
                System.out.printf("%-5d | %-20s | %-10d | %-8d\n",
                        med.getId(), med.getNome(), med.getEstoque(), med.getEstoqueMinimo());
            }
        }
    }

    // ===================================================================
    // MÉTODOS UTILITÁRIOS DA VIEW
    // ===================================================================

    private static int lerOpcaoInt() {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Erro: Digite um número válido.");
            return -1; // Retorna -1 para indicar opção inválida
        }
    }

    private static BigDecimal lerOpcaoBigDecimal() {
        try {
            String input = scanner.nextLine().replace(",", ".");
            if (input.isBlank()) {
                System.out.println("Erro: Valor não pode ser vazio.");
                return BigDecimal.ZERO; // Retorna zero para falha
            }
            return new BigDecimal(input);
        } catch (NumberFormatException e) {
            System.out.println("Erro: Digite um valor numérico válido (ex: 10.99).");
            return BigDecimal.ZERO; // Retorna zero para falha
        }
    }

    private static String lerString() {
        return scanner.nextLine();
    }

    private static void pressionarEnterParaContinuar() {
        System.out.println("\nPressione [ENTER] para continuar...");
        scanner.nextLine();
    }
}