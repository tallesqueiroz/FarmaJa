package br.com.projeto.farmaja;

import br.com.projeto.farmaja.view.MenuPrincipal;

public class Aplicacao {
    public static void main(String[] args) {
        System.out.println("--- Iniciando FarmaJá ---");
        MenuPrincipal menu = new MenuPrincipal();
        menu.iniciar();
        System.out.println("--- FarmaJá Encerrado ---");
    }
}