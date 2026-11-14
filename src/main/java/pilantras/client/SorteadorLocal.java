package pilantras.client;

import jakarta.enterprise.context.ApplicationScoped;
import pilantras.domain.entities.JogadorSessao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class SorteadorLocal implements Sorteador {

    @Override
    public Times sortear(Long sessaoId, List<JogadorSessao> jogadores) {
        List<JogadorSessao> embaralhados = new ArrayList<>(jogadores);

        Collections.shuffle(embaralhados);

        int metade = embaralhados.size() / 2;

        return new Times(
                sessaoId,
                embaralhados.subList(0, metade),
                embaralhados.subList(metade, embaralhados.size())
        );
    }

}