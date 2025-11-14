package br.com.projeto.farmaja.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Medicamento {
    // 1. ATRIBUTOS (colunas do banco)
    private Integer id;
    private String codigo;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer estoque;
    private Integer estoqueMinimo;
    private Integer fornecedorId;
    private Boolean requerReceita;
    private Boolean ativo;
    private LocalDateTime dataCriacao;

    // 2. CONSTRUTOR VAZIO (obrigatório para os DAOs)
    public Medicamento() {}

    // 3. CONSTRUTOR COM PARÂMETROS (criação de objetos)
    public Medicamento(String codigo, String nome, String descricao, BigDecimal preco,
                       Integer estoque, Integer estoqueMinimo, Integer fornecedorId,
                       Boolean requerReceita) {
        setCodigo(codigo);
        setNome(nome);
        setDescricao(descricao);
        setPreco(preco);
        setEstoque(estoque);
        setEstoqueMinimo(estoqueMinimo);
        setFornecedorId(fornecedorId);
        setRequerReceita(requerReceita != null ? requerReceita : Boolean.FALSE);
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
    }

    // 4. GETTERS E SETTERS (para todos os atributos)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("Código obrigatório");
        this.codigo = codigo;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome obrigatório");
        this.nome = nome;
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) {
        if (preco == null || preco.signum() < 0) throw new IllegalArgumentException("Preço não pode ser negativo");
        this.preco = preco;
    }

    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) {
        if (estoque == null || estoque < 0) throw new IllegalArgumentException("Estoque não pode ser negativo");
        this.estoque = estoque;
    }

    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) {
        if (estoqueMinimo == null || estoqueMinimo < 0) throw new IllegalArgumentException("Estoque mínimo inválido");
        this.estoqueMinimo = estoqueMinimo;
    }

    public Integer getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(Integer fornecedorId) {
        if (fornecedorId == null) throw new IllegalArgumentException("fornecedorId obrigatório");
        this.fornecedorId = fornecedorId;
    }

    public Boolean getRequerReceita() { return requerReceita; }
    public void setRequerReceita(Boolean requerReceita) { this.requerReceita = requerReceita; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    // 5. REGRAS DE NEGÓCIO DE APOIO
    public boolean estoqueBaixo() {
        return estoque != null && estoqueMinimo != null && estoque <= estoqueMinimo;
    }

    public void aumentarEstoque(int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
        this.estoque = (this.estoque == null ? 0 : this.estoque) + quantidade;
    }

    public void reduzirEstoque(int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
        int atual = (this.estoque == null ? 0 : this.estoque);
        if (atual - quantidade < 0) throw new IllegalStateException("Estoque insuficiente");
        this.estoque = atual - quantidade;
    }

    // 6. toString() (para exibir informações formatadas)
    @Override
    public String toString() {
        return String.format("ID: %d | Código: %s | Nome: %s | Preço: %s | Estoque: %d",
                id, codigo, nome, preco, estoque);
    }
}