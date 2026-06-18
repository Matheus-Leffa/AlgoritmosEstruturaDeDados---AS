package modelo.casa;

import modelo.Jogador;
import modelo.TipoPersonagem;

/**
 * Representa um imóvel comprável no tabuleiro (terrenos, empresas, etc.).
 */
public class CasaImovel extends Casa {
    private double precoCompra;
    private double aluguelBase;
    private double aluguelPorCasa;
    private double custoConstrucaoCasa;
    private int quantidadeCasas;
    private boolean temHotel;
    private String corGrupo;
    private Jogador proprietario;

    public CasaImovel(int id, String nome, String descricao, double precoCompra, double aluguelBase,
                      double aluguelPorCasa, double custoConstrucaoCasa, String corGrupo) {
        super(id, nome, descricao);
        this.precoCompra = precoCompra;
        this.aluguelBase = aluguelBase;
        this.aluguelPorCasa = aluguelPorCasa;
        this.custoConstrucaoCasa = custoConstrucaoCasa;
        this.corGrupo = corGrupo;
        this.quantidadeCasas = 0;
        this.temHotel = false;
        this.proprietario = null;
    }

    public double getPrecoCompra() {
        return precoCompra;
    }

    public void setPrecoCompra(double precoCompra) {
        this.precoCompra = precoCompra;
    }

    public double getAluguelBase() {
        if (proprietario != null && proprietario.getTipoPersonagem() == TipoPersonagem.CONSTRUTOR) {
            return aluguelBase * 1.15;
        }
        return aluguelBase;
    }

    public double getAluguelBaseOriginal() {
        return aluguelBase;
    }

    public void setAluguelBase(double aluguelBase) {
        this.aluguelBase = aluguelBase;
    }

    public double getAluguelPorCasa() {
        return aluguelPorCasa;
    }

    public void setAluguelPorCasa(double aluguelPorCasa) {
        this.aluguelPorCasa = aluguelPorCasa;
    }

    public double getCustoConstrucaoCasa() {
        return custoConstrucaoCasa;
    }

    public void setCustoConstrucaoCasa(double custoConstrucaoCasa) {
        this.custoConstrucaoCasa = custoConstrucaoCasa;
    }

    public int getQuantidadeCasas() {
        return quantidadeCasas;
    }

    public void setQuantidadeCasas(int quantidadeCasas) {
        this.quantidadeCasas = quantidadeCasas;
    }

    public boolean isTemHotel() {
        return temHotel;
    }

    public void setTemHotel(boolean temHotel) {
        this.temHotel = temHotel;
    }

    public String getCorGrupo() {
        return corGrupo;
    }

    public void setCorGrupo(String corGrupo) {
        this.corGrupo = corGrupo;
    }

    public Jogador getProprietario() {
        return proprietario;
    }

    public void setProprietario(Jogador proprietario) {
        this.proprietario = proprietario;
    }

    /**
     * Calcula o valor do aluguel atual com base no número de casas ou se possui hotel.
     */
    public double calcularAluguelAtual() {
        double base = getAluguelBase();
        if (temHotel) {
            return base + (aluguelPorCasa * 5); // Supondo hotel equivale a 5 casas em rentabilidade
        }
        return base + (aluguelPorCasa * quantidadeCasas);
    }

    @Override
    public String toString() {
        String statusProprietario = (proprietario == null) ? "Disponível para Compra" : "Dono: " + proprietario.getNome();
        String melhorias = temHotel ? "Hotel" : quantidadeCasas + " Casa(s)";
        return super.toString() + String.format(" [Grupo: %s | Preço: R$ %.2f | Aluguel Atual: R$ %.2f | %s | %s]",
                corGrupo, precoCompra, calcularAluguelAtual(), melhorias, statusProprietario);
    }
}
