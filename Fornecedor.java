package br.com.projeto.farmaja.model;

public class Fornecedor {
    // 1. ATRIBUTOS (colunas do banco)
    private Integer id;
    private String nome;
    private String cnpj;
    private String telefone;
    private String email;
    private Boolean ativo;

    // 2. CONSTRUTOR VAZIO (obrigatório para os DAOs)
    public Fornecedor() {}

    // 3. CONSTRUTOR COM PARÂMETROS (criação de objetos)
    public Fornecedor(String nome, String cnpj, String telefone, String email) {
        setNome(nome);
        setCnpj(cnpj);
        setTelefone(telefone);
        setEmail(email);
        this.ativo = true;
    }

    // 4. GETTERS E SETTERS (para todos os atributos)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome obrigatório");
        this.nome = nome;
    }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) throw new IllegalArgumentException("CNPJ obrigatório");
        this.cnpj = cnpj;
    }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    // 5. toString() (para exibir informações formatadas)
    @Override
    public String toString() {
        return String.format("ID: %d | Nome: %s | CNPJ: %s | Ativo: %s",
                id, nome, cnpj, String.valueOf(ativo));
    }
}
