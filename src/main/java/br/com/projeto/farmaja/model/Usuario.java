package br.com.projeto.farmaja.model;

import java.time.LocalDateTime;

public class Usuario {
    // 1. ATRIBUTOS (colunas do banco)
    private Integer id;
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private String telefone;
    private String tipoUsuario; // ADMINISTRADOR, CLIENTE, ENTREGADOR
    private Boolean ativo;
    private LocalDateTime dataCriacao;

    // 2. CONSTRUTOR VAZIO (obrigatório para os DAOs)
    public Usuario() {}

    // 3. CONSTRUTOR COM PARÂMETROS (criação de objetos)
    public Usuario(String nome, String email, String senha, String cpf, String telefone, String tipoUsuario) {
        setNome(nome);
        setEmail(email);
        setSenha(senha);
        setCpf(cpf);
        setTelefone(telefone);
        setTipoUsuario(tipoUsuario);
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
    }

    // 4. GETTERS E SETTERS (para todos os atributos)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome obrigatório");
        this.nome = nome;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email obrigatório");
        this.email = email.trim().toLowerCase();
    }

    public String getSenha() { return senha; }
    public void setSenha(String senha) {
        if (senha == null || senha.isBlank()) throw new IllegalArgumentException("Senha obrigatória");
        this.senha = senha;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) throw new IllegalArgumentException("CPF obrigatório");
        this.cpf = cpf;
    }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) {
        if (tipoUsuario == null || tipoUsuario.isBlank()) throw new IllegalArgumentException("Tipo de usuário obrigatório");
        String t = tipoUsuario.toUpperCase();
        if (!t.equals("ADMINISTRADOR") && !t.equals("CLIENTE") && !t.equals("ENTREGADOR")) {
            throw new IllegalArgumentException("Tipo de usuário inválido");
        }
        this.tipoUsuario = t;
    }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    // 5. toString() (para exibir informações formatadas)
    @Override
    public String toString() {
        return String.format("ID: %d | Nome: %s | Email: %s | Tipo: %s | Ativo: %s",
                id, nome, email, tipoUsuario, String.valueOf(ativo));
    }
}
