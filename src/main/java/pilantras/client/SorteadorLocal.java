package pilantras.client;

import jakarta.enterprise.context.ApplicationScoped;
import pilantras.domain.entities.JogadorSessao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class SorteadorLocal implements Sorteador {

    @Override
    public Times sortear(Long sessaoId, List<JogadorSessao> jogadores) {

        List<JogadorSessao> ordenados = new ArrayList<>(jogadores);
        Collections.shuffle(ordenados);

        ordenados.sort(
                Comparator.comparingInt(j -> -j.categoria.peso())
        );

        int[] forcas = new int[2];

        List<List<JogadorSessao>> times = new ArrayList<>();
        times.add(new ArrayList<>());
        times.add(new ArrayList<>());

        for (JogadorSessao jogador : ordenados) {
            int fraco = forcas[0] <= forcas[1] ? 0 : 1;
            times.get(fraco).add(jogador);
            forcas[fraco] += jogador.categoria.peso();
        }

        return new Times(sessaoId, times.get(0), times.get(1));
    }
}