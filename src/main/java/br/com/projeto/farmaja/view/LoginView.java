package br.com.projeto.farmaja.view;

import br.com.projeto.farmaja.view.util.LeitorConsole;

public class LoginView {

    public boolean realizarLogin(String tipoUsuario) {
        System.out.println("\n--- LOGIN (" + tipoUsuario + ") ---");
        String email = LeitorConsole.lerString("Digite seu email: ");
        String senha = LeitorConsole.lerString("Digite sua senha: ");

        if (tipoUsuario.equals("Cliente") && email.equals("cliente@farmaja.com") && senha.equals("cliente123")) {
            System.out.println("Login como Cliente bem-sucedido!");
            return true;
        }

        if (tipoUsuario.equals("Admin") && email.equals("admin@farmaja.com") && senha.equals("admin123")) {
            System.out.println("Login como Admin bem-sucedido!");
            return true;
        }

        if (tipoUsuario.equals("Entregador") && email.equals("entregador@farmaja.com") && senha.equals("entregador123")) {
            System.out.println("Login como Entregador bem-sucedido!");
            return true;
        }

        System.out.println("Email ou senha inválidos. Tente novamente.");
        return false;
    }
}