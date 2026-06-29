package modelo;

import estrutura.NoTabuleiro;
import modelo.casa.CasaImovel;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um jogador do jogo estratégico, contendo seus dados,
 * carteira de imóveis, histórico de movimento e regras específicas de seu personagem.
 */
public class Jogador {
    // Atributos obrigatórios solicitados
    private String nome;
    private double saldo;
    private NoTabuleiro posicaoAtual;   //posiçãoAtual
    private NoTabuleiro posicaoAnterior; //posiçãoAnterior
    private List<CasaImovel> propriedades;
    private int voltasCompletas;
    private boolean falido;

    // Atributos adicionais necessários para o funcionamento e personagens
    private String id;
    private TipoPersonagem tipoPersonagem;
    private boolean preso;

    public Jogador(String id, String nome, double saldoInicial, TipoPersonagem tipoPersonagem) {
        this.id = id;
        this.nome = nome;
        this.saldo = saldoInicial;
        this.tipoPersonagem = tipoPersonagem;
        this.propriedades = new ArrayList<>();
        this.voltasCompletas = 0;
        this.falido = false;
        this.preso = false;
        this.posicaoAtual = null;
        this.posicaoAnterior = null;
    }

    // --- GETTERS E SETTERS OBRIGATÓRIOS E AUXILIARES ---

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public NoTabuleiro getPosicaoAtual() {
        return posicaoAtual;
    }

    /**
     * Define a nova posição atual do jogador, guardando a posição antiga
     * como a posição anterior (histórico de movimentação).
     */
    public void setPosicaoAtual(NoTabuleiro novaPosicao) {
        this.posicaoAnterior = this.posicaoAtual;
        this.posicaoAtual = novaPosicao;
    }

    public NoTabuleiro getPosicaoAnterior() {
        return posicaoAnterior;
    }

    public void setPosicaoAnterior(NoTabuleiro posicaoAnterior) {
        this.posicaoAnterior = posicaoAnterior;
    }

    public List<CasaImovel> getPropriedades() {
        return propriedades;
    }

    public int getVoltasCompletas() {
        return voltasCompletas;
    }

    public void setVoltasCompletas(int voltasCompletas) {
        this.voltasCompletas = voltasCompletas;
    }

    public boolean isFalido() {
        return falido;
    }

    public void setFalido(boolean falido) {
        this.falido = falido;
    }

    public String getId() {
        return id;
    }

    public TipoPersonagem getTipoPersonagem() {
        return tipoPersonagem;
    }

    public boolean isPreso() {
        return preso;
    }

    /**
     * Define o estado de prisão do jogador.
     * Caso o personagem seja um ADVOGADO, ele ignora e anula qualquer tentativa de prisão.
     */
    public void setPreso(boolean preso) {
        if (preso && tipoPersonagem == TipoPersonagem.ADVOGADO) {
            System.out.println("   [HABILIDADE: ADVOGADO] " + nome + " é Advogado e usou sua influência para anular a ordem de prisão!");
            this.preso = false;
        } else {
            this.preso = preso;
            if (preso) {
                System.out.println("   [SISTEMA] " + nome + " foi enviado para a prisão!");
            } else {
                System.out.println("   [SISTEMA] " + nome + " foi libertado da prisão!");
            }
        }
    }

    // --- MÉTODOS DE AÇÕES DO JOGO ---

    /**
     * Adiciona fundos ao saldo do jogador.
     */
    public void adicionarSaldo(double valor) {
        if (this.falido || valor <= 0) return;
        this.saldo += valor;
    }

    /**
     * Deduz fundos do saldo do jogador. Se o saldo ficar abaixo de zero,
     * o jogador é declarado falido.
     */
    public void pagar(double valor) {
        if (this.falido || valor <= 0) return;
        
        if (this.saldo >= valor) {
            this.saldo -= valor;
        } else {
            this.saldo = 0;
            this.falido = true;
            System.out.println("   [FALÊNCIA] " + nome + " declarou falência ao não conseguir pagar o valor integral de R$ " + valor);
        }
    }

    /**
     * Processa o pagamento de imposto.
     * ESPECULADOR paga 10% a mais de imposto.
     */
    public void pagarImposto(double valorBase) {
        double valorFinal = valorBase;
        if (tipoPersonagem == TipoPersonagem.ESPECULADOR) {
            valorFinal = valorBase * 1.10;
            System.out.println("   [HABILIDADE: ESPECULADOR] " + nome + " paga +10% de imposto (Total: R$ " + valorFinal + ")");
        }
        pagar(valorFinal);
    }

    /**
     * Processa a transferência de aluguel para outro jogador.
     * NEGOCIANTE paga 10% a menos de aluguel.
     */
    public double pagarAluguel(Jogador proprietario, double valorBase) {
        if (this.falido) return 0.0;
        
        double valorFinal = valorBase;
        if (tipoPersonagem == TipoPersonagem.NEGOCIANTE) {
            valorFinal = valorBase * 0.90;
            System.out.println("   [HABILIDADE: NEGOCIANTE] " + nome + " paga -10% de aluguel (Total: R$ " + valorFinal + ")");
        }

        if (this.saldo >= valorFinal) {
            this.saldo -= valorFinal;
            proprietario.adicionarSaldo(valorFinal);
            System.out.println("   [ALUGUEL] " + nome + " pagou R$ " + valorFinal + " para " + proprietario.getNome());
            return valorFinal;
        } else {
            // Falência ao pagar aluguel (transfere o resto dos fundos)
            double transferido = this.saldo;
            this.saldo = 0;
            this.falido = true;
            proprietario.adicionarSaldo(transferido);
            System.out.println("   [FALÊNCIA] " + nome + " não possui saldo suficiente (R$ " + valorFinal + 
                               "). Transferiu R$ " + transferido + " para " + proprietario.getNome() + " e faliu.");
            return transferido;
        }
    }

    /**
     * Processa o recebimento de salário ou bonificação de passagem.
     * ESPECULADOR recebe 20% a mais de salário ao completar uma volta.
     */
    public void receberSalario(double valorBase) {
        double valorFinal = valorBase;
        if (tipoPersonagem == TipoPersonagem.ESPECULADOR) {
            valorFinal = valorBase * 1.20;
            System.out.println("   [HABILIDADE: ESPECULADOR] " + nome + " recebe +20% de bonificação de volta (Total: R$ " + valorFinal + ")");
        }
        adicionarSaldo(valorFinal);
        System.out.println("   [SALÁRIO] " + nome + " recebeu R$ " + valorFinal);
    }

    /**
     * Incrementa o contador de voltas completadas do jogador.
     */
    public void incrementarVoltas() {
        this.voltasCompletas++;
        System.out.println("   [VOLTA] " + nome + " completou " + voltasCompletas + " volta(s) no tabuleiro!");
    }

    /**
     * Compra e anexa uma propriedade à lista do jogador.
     */
    public void comprarPropriedade(CasaImovel propriedade) {
        if (this.falido || propriedade == null) return;
        
        double preco = propriedade.getPrecoCompra();
        if (this.saldo >= preco) {
            this.saldo -= preco;
            propriedades.add(propriedade);
            propriedade.setProprietario(this);
            System.out.println("   [COMPRA] " + nome + " comprou " + propriedade.getNome() + " por R$ " + preco);
            if (tipoPersonagem == TipoPersonagem.CONSTRUTOR) {
                System.out.println("   [HABILIDADE: CONSTRUTOR] Imóvel comprado por Construtor! Aluguel base aumentado em 15% (Novo aluguel base: R$ " 
                                   + propriedade.getAluguelBase() + ")");
            }
        } else {
            System.out.println("   [COMPRA] " + nome + " não possui R$ " + preco + " para comprar " + propriedade.getNome());
        }
    }

    /**
     * Perde todas as propriedades (usado em caso de falência).
     */
    public void desapropriarTudo() {
        for (CasaImovel p : propriedades) {
            p.setProprietario(null);
            p.setQuantidadeCasas(0);
            p.setTemHotel(false);
        }
        propriedades.clear();
    }

    @Override
    public String toString() {
        String status = falido ? "FALIDO" : String.format("R$ %.2f", saldo);
        String pos = (posicaoAtual != null) ? posicaoAtual.getCasa().getNome() : "Fora do tabuleiro";
        String statusPrisão = preso ? " (PRESO)" : "";
        return String.format("%s (%s) [%s | Voltas: %d | Pos: %s]%s", 
                nome, tipoPersonagem, status, voltasCompletas, pos, statusPrisão);
    }
}
