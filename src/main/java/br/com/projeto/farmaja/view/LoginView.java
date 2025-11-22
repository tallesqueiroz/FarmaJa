package br.com.projeto.farmaja.view;

import br.com.projeto.farmaja.controller.UsuarioController;
import br.com.projeto.farmaja.dao.EnderecoDAO;
import br.com.projeto.farmaja.dao.UsuarioDAO;
import br.com.projeto.farmaja.model.Usuario;
import br.com.projeto.farmaja.view.util.LeitorConsole;

public class LoginView {

    private final UsuarioController usuarioController;
    private Usuario usuarioAutenticado = null;

    public LoginView() {
        // Inicializa o controller para acessar o banco de dados
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        EnderecoDAO enderecoDAO = new EnderecoDAO();
        this.usuarioController = new UsuarioController(usuarioDAO, enderecoDAO);
    }

    /**
     * Realiza login validando no banco de dados
     * @param tipoUsuario Tipo esperado (Cliente, Admin, Entregador)
     * @return true se login bem-sucedido, false caso contrário
     */
    public boolean realizarLogin(String tipoUsuario) {
        System.out.println("\n--- LOGIN (" + tipoUsuario + ") ---");
        String email = LeitorConsole.lerString("Digite seu email: ");
        String senha = LeitorConsole.lerString("Digite sua senha: ");

        // Tenta autenticar no banco de dados
        Usuario usuario = usuarioController.login(email, senha);

        if (usuario == null) {
            System.out.println("Email ou senha inválidos. Tente novamente.");
            return false;
        }

        // Verifica se o tipo do usuário corresponde ao menu selecionado
        String tipoEsperado = mapearTipoUsuario(tipoUsuario);

        if (!usuario.getTipoUsuario().equals(tipoEsperado)) {
            System.out.println("Este usuário não tem permissão para acessar este menu.");
            System.out.println("   Tipo do usuário: " + formatarTipo(usuario.getTipoUsuario()));
            System.out.println("   Acesso esperado: " + tipoUsuario);
            return false;
        }

        // Verifica se o usuário está ativo
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            System.out.println("Este usuário está inativo. Entre em contato com o administrador.");
            return false;
        }

        // Login bem-sucedido
        this.usuarioAutenticado = usuario;
        System.out.println("Login como " + tipoUsuario + " bem-sucedido!");
        System.out.println("   Bem-vindo(a), " + usuario.getNome() + "!");
        return true;
    }

    /**
     * Mapeia o nome exibido para o tipo no banco de dados
     */
    private String mapearTipoUsuario(String tipoExibido) {
        return switch (tipoExibido) {
            case "Cliente" -> "CLIENTE";
            case "Admin", "SuperAdmin" -> "ADMINISTRADOR";
            case "Entregador" -> "ENTREGADOR";
            default -> tipoExibido.toUpperCase();
        };
    }

    /**
     * Formata o tipo para exibição amigável
     */
    private String formatarTipo(String tipo) {
        return switch (tipo) {
            case "CLIENTE" -> "Cliente";
            case "ADMINISTRADOR" -> "Administrador";
            case "ENTREGADOR" -> "Entregador";
            default -> tipo;
        };
    }

    /**
     * Retorna o usuário autenticado (para ser usado nos menus)
     */
    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    /**
     * Limpa o usuário autenticado (logout)
     */
    public void logout() {
        this.usuarioAutenticado = null;
    }
}