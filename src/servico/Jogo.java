package servico;

import estrutura.ListaDuplamenteLigadaCircular;
import estrutura.NoTabuleiro;
import modelo.Jogador;
import modelo.casa.*;
import modelo.carta.Baralho;
import modelo.carta.Carta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Motor central de execução e controle da partida do jogo Monopoly estratégico.
 * Gerencia rodadas, turnos, rolagem de dados, movimentações, efeitos de casas
 * e transações financeiras (compras, aluguéis, impostos e falências).
 */
public class Jogo {
    private ListaDuplamenteLigadaCircular tabuleiro;
    private Baralho baralho;
    private List<Jogador> jogadoresAtivos;
    private List<Jogador> jogadoresFalidos;
    private int rodada;
    private Random random;
    private static final double SALARIO_BASE = 200.0;

    public Jogo(ListaDuplamenteLigadaCircular tabuleiro, List<Jogador> jogadoresIniciais) {
        this.tabuleiro = tabuleiro;
        this.baralho = new Baralho();
        this.jogadoresAtivos = new ArrayList<>(jogadoresIniciais);
        this.jogadoresFalidos = new ArrayList<>();
        this.rodada = 1;
        this.random = new Random();

        // Aloca todos os jogadores no ponto inicial do tabuleiro (Cabeça da lista circular)
        for (Jogador j : jogadoresAtivos) {
            j.setPosicaoAtual(tabuleiro.getCabeca());
        }
    }

    /**
     * Inicia o loop principal de execução da partida, rodando rodadas sucessivas
     * até que reste apenas 1 jogador ativo no jogo.
     */
    public void iniciarPartida() {
        System.out.println("\n=========================================================");
        System.out.println("   PARTIDA INICIADA! QUE VENÇA O MELHOR ESTRATEGISTA!    ");
        System.out.println("=========================================================\n");

        while (!isPartidaEncerrada()) {
            jogarRodada();
        }

        exibirRelatorioFinal();
    }

    /**
     * Verifica se a partida foi encerrada.
     */
    public boolean isPartidaEncerrada() {
        return jogadoresAtivos.size() <= 1 || rodada > 100;
    }

    /**
     * Executa uma rodada completa da partida.
     * Cada jogador ativo realiza o seu respectivo turno na ordem da lista.
     */
    private void jogarRodada() {
        System.out.println("\n---------------------------------------------------------");
        System.out.println(" >>> INICIANDO RODADA #" + rodada + " <<<");
        System.out.println("---------------------------------------------------------");

        // Cria uma cópia da lista para iteração segura caso ocorram falências no turno
        List<Jogador> copiaJogadores = new ArrayList<>(jogadoresAtivos);

        for (Jogador jogador : copiaJogadores) {
            if (jogador.isFalido()) {
                continue;
            }
            executarTurno(jogador);

            if (isPartidaEncerrada()) {
                break;
            }
        }
        rodada++;
    }

    /**
     * Executa as ações de turno de um jogador específico.
     */
    private void executarTurno(Jogador jogador) {
        System.out.println("\n[TURNO] Vez de: " + jogador.getNome() + " (" + jogador.getTipoPersonagem() + ")");
        System.out.println("   Saldo Atual: R$ " + jogador.getSaldo() + " | Pos: " + jogador.getPosicaoAtual().getCasa().getNome());

        // Tratamento da prisão
        if (jogador.isPreso()) {
            processarTentativaFugaPrisao(jogador);
            verificarFalenciasGerais();
            // Se continuar preso ou faliu após a tentativa, encerra o turno dele aqui
            if (jogador.isPreso() || jogador.isFalido()) {
                return;
            }
        }

        // 1. Rolagem de dois dados (requisito)
        int dado1 = rolarDado();
        int dado2 = rolarDado();
        int totalCasas = dado1 + dado2;
        System.out.println("   [DADOS] Rolo de dados: [" + dado1 + ", " + dado2 + "] -> Total: " + totalCasas + " casas.");

        // 2. Movimentação no tabuleiro (com passagem pelo início e salário)
        moverJogador(jogador, totalCasas);

        // 3. Execução dos efeitos da casa onde o jogador pousou
        aplicarEfeitoCasa(jogador, jogador.getPosicaoAtual().getCasa());

        // 4. Verificação pós-turno de falência geral (afeta o jogador da vez e outros)
        verificarFalenciasGerais();
    }

    /**
     * Rola um dado padrão de 6 lados.
     */
    private int rolarDado() {
        return random.nextInt(6) + 1;
    }

    /**
     * Move o jogador pelo tabuleiro e aciona a callback de passagem pelo início.
     */
    private void moverJogador(Jogador jogador, int passos) {
        NoTabuleiro antigaPosicao = jogador.getPosicaoAtual();
        NoTabuleiro novaPosicao = tabuleiro.moverEVerificarPassagemInicio(antigaPosicao, passos, () -> {
            // Callback: jogador cruza o início do tabuleiro
            System.out.println("   [TABULEIRO] " + jogador.getNome() + " cruzou a linha de partida!");
            jogador.incrementarVoltas();
            jogador.receberSalario(SALARIO_BASE);
        });
        jogador.setPosicaoAtual(novaPosicao);
        System.out.println("   [TABULEIRO] " + jogador.getNome() + " moveu-se de " + antigaPosicao.getCasa().getNome() + " para " + novaPosicao.getCasa().getNome());
    }

    /**
     * Distribui a execução do efeito com base no tipo concreto da Casa.
     */
    private void aplicarEfeitoCasa(Jogador jogador, Casa casa) {
        if (casa instanceof CasaInicio) {
            System.out.println("   [CASA] Parou no Ponto de Partida para descansar.");
        } else if (casa instanceof CasaImovel) {
            processarCasaImovel(jogador, (CasaImovel) casa);
        } else if (casa instanceof CasaImposto) {
            processarCasaImposto(jogador, (CasaImposto) casa);
        } else if (casa instanceof CasaRestituicao) {
            processarCasaRestituicao(jogador, (CasaRestituicao) casa);
        } else if (casa instanceof CasaSorteReves) {
            processarCasaSorteReves(jogador, (CasaSorteReves) casa);
        }
    }

    /**
     * Trata a tentativa de fuga da prisão.
     */
    private void processarTentativaFugaPrisao(Jogador jogador) {
        System.out.println("   [PRISÃO] " + jogador.getNome() + " está preso. Tentando rolar dados duplos para fuga livre...");
        int d1 = rolarDado();
        int d2 = rolarDado();
        System.out.println("   [PRISÃO] Dados: [" + d1 + ", " + d2 + "]");
        
        if (d1 == d2) {
            System.out.println("   [PRISÃO] Fuga de sucesso! Dados idênticos. Jogador livre!");
            jogador.setPreso(false);
        } else {
            System.out.println("   [PRISÃO] Falhou em rolar duplos. Pagando fiança compulsória de R$ 50,00.");
            jogador.pagar(50.0);
            if (!jogador.isFalido()) {
                jogador.setPreso(false);
            }
        }
    }

    /**
     * Trata o pouso sobre um espaço de Imóvel (Compra, Construção ou Cobrança de Aluguel).
     */
    private void processarCasaImovel(Jogador jogador, CasaImovel imovel) {
        Jogador proprietario = imovel.getProprietario();

        if (proprietario == null) {
            // Imóvel disponível para compra
            System.out.println("   [CASA] " + imovel.getNome() + " está à venda por R$ " + imovel.getPrecoCompra());
            if (jogador.getSaldo() >= imovel.getPrecoCompra()) {
                jogador.comprarPropriedade(imovel);
            } else {
                System.out.println("   [CASA] Saldo insuficiente para efetuar a compra.");
            }
        } else if (proprietario == jogador) {
            // Próprio jogador é dono: tenta fazer melhorias
            System.out.println("   [CASA] Você parou na sua própria propriedade: " + imovel.getNome());
            tentarConstruirNoImovel(jogador, imovel);
        } else {
            // Pertence a outro jogador: cobra aluguel
            double valorAluguel = imovel.calcularAluguelAtual();
            System.out.println("   [CASA] Propriedade de " + proprietario.getNome() + ". Taxa de aluguel: R$ " + valorAluguel);
            jogador.pagarAluguel(proprietario, valorAluguel);
        }
    }

    /**
     * Tenta construir casas ou hotel em uma propriedade já possuída.
     */
    private void tentarConstruirNoImovel(Jogador jogador, CasaImovel imovel) {
        double custo = imovel.getCustoConstrucaoCasa();
        if (!imovel.isTemHotel()) {
            if (imovel.getQuantidadeCasas() < 4) {
                if (jogador.getSaldo() >= custo) {
                    jogador.pagar(custo);
                    imovel.setQuantidadeCasas(imovel.getQuantidadeCasas() + 1);
                    System.out.println("   [MELHORIA] Construiu a " + imovel.getQuantidadeCasas() + "ª casa em " + imovel.getNome() + " por R$ " + custo);
                }
            } else {
                if (jogador.getSaldo() >= custo) {
                    jogador.pagar(custo);
                    imovel.setQuantidadeCasas(0);
                    imovel.setTemHotel(true);
                    System.out.println("   [MELHORIA] Construiu um HOTEL em " + imovel.getNome() + " por R$ " + custo);
                }
            }
        }
    }

    /**
     * Executa a cobrança de imposto do jogador.
     */
    private void processarCasaImposto(Jogador jogador, CasaImposto imposto) {
        double taxa = imposto.getValorImposto();
        System.out.println("   [CASA] Landed on tax space. Cobrança de: R$ " + taxa);
        jogador.pagarImposto(taxa);
    }

    /**
     * Aplica o recebimento de restituição financeira ao jogador.
     */
    private void processarCasaRestituicao(Jogador jogador, CasaRestituicao restituicao) {
        double valor = restituicao.getValorRestituicao();
        System.out.println("   [CASA] Landed on tax refund. Crédito de: R$ " + valor);
        jogador.adicionarSaldo(valor);
    }

    /**
     * Saca e executa a carta do baralho.
     */
    private void processarCasaSorteReves(Jogador jogador, CasaSorteReves casaSorteReves) {
        Carta carta = baralho.sacar();
        carta.executar(jogador, jogadoresAtivos, tabuleiro);
        verificarFalenciasGerais();
    }

    /**
     * Verifica as contas de todos os jogadores para aplicar falências gerais.
     * Necessário caso cartas de Sorte/Revés forcem retiradas que levem o saldo de outros jogadores a ficarem negativos.
     */
    private void verificarFalenciasGerais() {
        List<Jogador> copia = new ArrayList<>(jogadoresAtivos);
        for (Jogador j : copia) {
            if (j.getSaldo() < 0 || j.isFalido()) {
                if (j.getSaldo() < 0) {
                    j.setSaldo(0);
                    j.setFalido(true);
                }
                declararFalenciaJogador(j);
            }
        }
    }

    /**
     * Desapropria os bens do jogador e o remove da partida ativa.
     */
    private void declararFalenciaJogador(Jogador jogador) {
        System.out.println("\n[SISTEMA] Falência decretada para: " + jogador.getNome() + "!");
        jogador.desapropriarTudo();
        jogadoresAtivos.remove(jogador);
        jogadoresFalidos.add(jogador);
    }

    /**
     * Exibe estatísticas consolidadas e classificados da partida após a conclusão.
     */
    private void exibirRelatorioFinal() {
        System.out.println("\n=========================================================");
        System.out.println("               RELATÓRIO CONSOLIDADO DO JOGO             ");
        System.out.println("=========================================================\n");

        if (jogadoresAtivos.size() == 1) {
            Jogador vencedor = jogadoresAtivos.get(0);
            System.out.println("🏆 VENCEDOR DO JOGO POR ELIMINAÇÃO: " + vencedor.getNome().toUpperCase() + " (" + vencedor.getTipoPersonagem() + ")");
            System.out.println("💰 Saldo Acumulado: R$ " + vencedor.getSaldo());
            System.out.println("🔄 Rodadas Completas no Tabuleiro: " + vencedor.getVoltasCompletas() + " voltas.");
            System.out.println("🏠 Propriedades de Posse: " + vencedor.getPropriedades().size());
            for (CasaImovel p : vencedor.getPropriedades()) {
                String melhorias = p.isTemHotel() ? "Hotel" : p.getQuantidadeCasas() + " Casa(s)";
                System.out.println("   - " + p.getNome() + " (Melhorias: " + melhorias + " | Aluguel Atual: R$ " + p.calcularAluguelAtual() + ")");
            }
        } else if (jogadoresAtivos.size() > 1) {
            Jogador vencedor = null;
            double maiorPatrimonio = -1.0;
            System.out.println("⏱️ LIMITE DE RODADAS ALCANÇADO (" + (rodada - 1) + " rodadas)!");
            System.out.println("--- PATRIMÔNIO DOS JOGADORES ATIVOS ---");
            for (Jogador j : jogadoresAtivos) {
                double patrimonio = j.getSaldo();
                for (CasaImovel p : j.getPropriedades()) {
                    patrimonio += p.getPrecoCompra();
                }
                System.out.printf(" - %s (%s): Saldo R$ %.2f + Imóveis R$ %.2f = Patrimônio Total R$ %.2f\n",
                    j.getNome(), j.getTipoPersonagem(), j.getSaldo(), (patrimonio - j.getSaldo()), patrimonio);
                
                if (patrimonio > maiorPatrimonio) {
                    maiorPatrimonio = patrimonio;
                    vencedor = j;
                }
            }
            if (vencedor != null) {
                System.out.println("\n🏆 VENCEDOR DO JOGO POR PATRIMÔNIO: " + vencedor.getNome().toUpperCase() + " (" + vencedor.getTipoPersonagem() + ")");
            }
        } else {
            System.out.println("O jogo encerrou abruptamente sem vencedores.");
        }

        System.out.println("\n--- ORDEM DE ELIMINAÇÃO POR FALÊNCIA ---");
        for (int i = 0; i < jogadoresFalidos.size(); i++) {
            Jogador f = jogadoresFalidos.get(i);
            System.out.println((i + 1) + "º Lugar Eliminado: " + f.getNome() + " (" + f.getTipoPersonagem() + ") - Durou " + f.getVoltasCompletas() + " voltas.");
        }
        System.out.println("\n=========================================================");
    }

    // Getters para monitoramento
    public List<Jogador> getJogadoresAtivos() {
        return jogadoresAtivos;
    }

    public List<Jogador> getJogadoresFalidos() {
        return jogadoresFalidos;
    }

    public int getRodada() {
        return rodada;
    }
}
