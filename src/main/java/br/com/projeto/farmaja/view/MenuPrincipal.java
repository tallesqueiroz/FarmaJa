package br.com.projeto.farmaja.view;


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
        boolean rodando = true;

        while (rodando) {
            System.out.println("\n===== BEM-VINDO AO FARMAJÁ =====");
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
                        new ClienteMenu().mostrar();
                    }
                    break;
                case 2:
                    sucessoLogin = loginView.realizarLogin("Admin");
                    if (sucessoLogin) {
                        new AdminMenu().mostrar();
                    }
                    break;
                case 3:
                    sucessoLogin = loginView.realizarLogin("Entregador");
                    if (sucessoLogin) {
                        System.out.println("Abrindo portal do Entregador...");
                        new EntregadorMenu().mostrar();
                    }
                    break;
                case 0:
                    rodando = false;
                    break;
                default:
                    System.out.println("Opção de perfil inválida. Tente novamente.");
                    break;
            }
        }

        System.out.println("Obrigado por usar o sistema. Até logo!");
    }
}