package pilantras.client;

import pilantras.domain.entities.JogadorSessao;

import java.util.List;

public interface Sorteador {

    Times sortear(Long sessaoId, List<JogadorSessao> jogadores);

    record Times(
            Long sessaoId,
            List<JogadorSessao> ladoA,
            List<JogadorSessao> ladoB
    ) { }

}
