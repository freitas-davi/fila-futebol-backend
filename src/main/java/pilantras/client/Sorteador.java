package pilantras.client;

import pilantras.domain.entities.JogadorSessao;

import java.util.List;

public interface Sorteador {

    Times sortear(List<JogadorSessao> jogadores);

    record Times(List<JogadorSessao> ladoA, List<JogadorSessao> ladoB) { }

}
