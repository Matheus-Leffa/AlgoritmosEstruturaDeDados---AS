package modelo;

/**
 * Enumeração contendo os tipos de personagens disponíveis no jogo estratégico,
 * cada um com suas respectivas habilidades passivas especiais.
 */
public enum TipoPersonagem {
    ESPECULADOR, // Recebe +20% salário ao completar volta, paga +10% imposto
    NEGOCIANTE,  // Paga -10% de aluguel
    ADVOGADO,    // Especial: imune à mecânica de prisão
    CONSTRUTOR   // Imóveis comprados rendem +15% de aluguel base
}
