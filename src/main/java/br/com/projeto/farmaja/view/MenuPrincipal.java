package br.com.projeto.farmaja.view;

import br.com.projeto.farmaja.database.DatabaseConnection;
import br.com.projeto.farmaja.view.util.LeitorConsole;
import br.com.projeto.farmaja.view.Cliente.ClienteMenu;
import br.com.projeto.farmaja.view.Admin.AdminMenu;
import br.com.projeto.farmaja.view.Entregador.EntregadorMenu;

public class MenuPrincipal {
    private LoginView loginView;

    public MenuPrincipal() {
        this.loginView = new LoginView();
    }

    public void iniciar() {
        // Inicializa o banco de dados na primeira execução
        System.out.println("Inicializando banco de dados...");
        DatabaseConnection.initializeDatabase();

        boolean rodando = true;

        while (rodando) {
            System.out.println("\n╔═════════════════════════════════╗");
            System.out.println("║   BEM-VINDO AO FARMAJÁ          ║");
            System.out.println("╚═════════════════════════════════╝");
            System.out.println("1. Sou Cliente");
            System.out.println("2. Sou SuperAdmin");
            System.out.println("3. Sou Entregador");
            System.out.println("0. Sair da Aplicação");

            int opcao = LeitorConsole.lerInteiro("Escolha uma opção: ");
            boolean sucessoLogin;

            switch (opcao) {
                case 1:
                    sucessoLogin = loginView.realizarLogin("Cliente");
                    if (sucessoLogin) {
                        new ClienteMenu(loginView.getUsuarioAutenticado()).mostrar();
                        loginView.logout(); // Limpa a sessão ao sair
                    }
                    break;
                case 2:
                    sucessoLogin = loginView.realizarLogin("Admin");
                    if (sucessoLogin) {
                        new AdminMenu(loginView.getUsuarioAutenticado()).mostrar();
                        loginView.logout();
                    }
                    break;
                case 3:
                    sucessoLogin = loginView.realizarLogin("Entregador");
                    if (sucessoLogin) {
                        new EntregadorMenu(loginView.getUsuarioAutenticado()).mostrar();
                        loginView.logout();
                    }
                    break;
                case 0:
                    rodando = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        }

        System.out.println("\nObrigado por usar o FarmaJá. Até logo!");
        DatabaseConnection.closeConnection();
    }
}