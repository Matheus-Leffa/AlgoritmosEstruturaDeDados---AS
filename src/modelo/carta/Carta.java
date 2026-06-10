package modelo.carta;

import modelo.Jogador;
import estrutura.ListaDuplamenteLigadaCircular;
import java.util.List;

/**
 * Representa uma carta individual de Sorte ou Revés no jogo.
 */
public class Carta {
    private int id;
    private String titulo;
    private String descricao;
    private TipoCarta tipo;
    private EfeitoCarta efeito;

    public Carta(int id, String titulo, String descricao, TipoCarta tipo, EfeitoCarta efeito) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.efeito = efeito;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoCarta getTipo() {
        return tipo;
    }

    public void setTipo(TipoCarta tipo) {
        this.tipo = tipo;
    }

    public EfeitoCarta getEfeito() {
        return efeito;
    }

    public void setEfeito(EfeitoCarta efeito) {
        this.efeito = efeito;
    }

    /**
     * Executa a ação da carta.
     * Exibe no console o título, a descrição e aciona o efeito programado.
     */
    public void executar(Jogador jogadorAtivo, List<Jogador> todosJogadores, ListaDuplamenteLigadaCircular tabuleiro) {
        System.out.println("\n---------------------------------------------------------");
        System.out.println("   CARTA SACADA: " + titulo + " [" + tipo + "]");
        System.out.println("   \"" + descricao + "\"");
        System.out.println("---------------------------------------------------------");
        
        if (efeito != null) {
            efeito.aplicar(jogadorAtivo, todosJogadores, tabuleiro);
        }
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s)", id, titulo, tipo);
    }
}
