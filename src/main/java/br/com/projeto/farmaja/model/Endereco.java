package br.com.projeto.farmaja.model;

public class Endereco {
    // 1. ATRIBUTOS (colunas do banco)
    private Integer id;
    private Integer usuarioId;
    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado; // UF
    private String cep;

    // 2. CONSTRUTOR VAZIO (obrigatório para os DAOs)
    public Endereco() {}

    // 3. CONSTRUTOR COM PARÂMETROS (criação de objetos)
    public Endereco(Integer usuarioId, String rua, String numero, String bairro, String cidade, String estado, String cep, String complemento) {
        setUsuarioId(usuarioId);
        setRua(rua);
        setNumero(numero);
        setBairro(bairro);
        setCidade(cidade);
        setEstado(estado);
        setCep(cep);
        this.complemento = complemento;
    }

    // 4. GETTERS E SETTERS (para todos os atributos)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) {
        if (usuarioId == null) throw new IllegalArgumentException("usuarioId obrigatório");
        this.usuarioId = usuarioId;
    }

    public String getRua() { return rua; }
    public void setRua(String rua) {
        if (rua == null || rua.isBlank()) throw new IllegalArgumentException("Rua obrigatória");
        this.rua = rua;
    }

    public String getNumero() { return numero; }
    public void setNumero(String numero) {
        if (numero == null || numero.isBlank()) throw new IllegalArgumentException("Número obrigatório");
        this.numero = numero;
    }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) {
        if (bairro == null || bairro.isBlank()) throw new IllegalArgumentException("Bairro obrigatório");
        this.bairro = bairro;
    }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) {
        if (cidade == null || cidade.isBlank()) throw new IllegalArgumentException("Cidade obrigatória");
        this.cidade = cidade;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) {
        if (estado == null || estado.isBlank()) throw new IllegalArgumentException("UF obrigatória");
        this.estado = estado.toUpperCase();
    }

    public String getCep() { return cep; }
    public void setCep(String cep) {
        if (cep == null || cep.isBlank()) throw new IllegalArgumentException("CEP obrigatório");
        this.cep = cep;
    }

    // 5. toString() (para exibir informações formatadas)
    @Override
    public String toString() {
        return String.format("ID: %d | Usuário: %d | %s, %s - %s, %s/%s | CEP: %s",
                id, usuarioId, rua, numero, bairro, cidade, estado, cep);
    }
}