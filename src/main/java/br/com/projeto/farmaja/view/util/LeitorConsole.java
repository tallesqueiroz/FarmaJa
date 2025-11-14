package br.com.projeto.farmaja.view.util;

import java.util.InputMismatchException;
import java.util.Scanner;

public class LeitorConsole {

    private static Scanner scanner = new Scanner(System.in);

    public static int lerInteiro(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int valor = scanner.nextInt();
                scanner.nextLine(); // Limpa o buffer (consome o "Enter" restante)
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("Erro: Entrada inválida. Por favor, digite um número inteiro.");
                scanner.nextLine(); // Limpa a entrada inválida do buffer
            }
        }
    }

    public static String lerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static boolean confirmar(String prompt) {
        while (true) {
            // Usa o lerString para pegar a resposta
            String resposta = lerString(prompt + " (S/N): ");

            if (resposta.equalsIgnoreCase("S")) {
                return true;
            } else if (resposta.equalsIgnoreCase("N")) {
                return false;
            } else {
                System.out.println("Resposta inválida. Digite 'S' para Sim ou 'N' para Não.");
            }
        }
    }
}