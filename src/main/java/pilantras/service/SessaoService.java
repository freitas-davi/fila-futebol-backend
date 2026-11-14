package pilantras.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;
import pilantras.client.Sorteador;
import pilantras.domain.entities.JogadorSessao;
import pilantras.domain.entities.Sessao;
import pilantras.domain.enums.LadoTime;
import pilantras.dto.PartidaResponseDTO;
import pilantras.repository.JogadorSessaoRepository;
import pilantras.repository.SessaoRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SessaoService {

    private static final Logger LOG = Logger.getLogger(SessaoService.class);
    private static final Duration LIMITE = Duration.ofHours(12);
    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    private final SessaoRepository repository;
    private final JogadorSessaoRepository jogadorRepository;

    public SessaoService(SessaoRepository repository, JogadorSessaoRepository jogadorRepository) {
        this.repository = repository;
        this.jogadorRepository = jogadorRepository;
    }

    @Transactional
    public Sessao obterOuCriarSessao() {
        LOG.info("Obtendo ou criando sessão ativa");
        Optional<Sessao> ativa = repository.buscarAtiva();

        if (ativa.isPresent()) {
            if (!expirada(ativa.get())) {
                LOG.infof("Sessão ativa encontrada: sessaoId=%d", ativa.get().id);
                return ativa.get();
            }
            LOG.infof("Encerrando sessão expirada: sessaoId=%d", ativa.get().id);
            ativa.get().status = Sessao.Status.ENCERRADA;
            repository.flush();
        }
        Sessao s = new Sessao();
        s.data = LocalDate.now(ZONA);
        s.status = Sessao.Status.ATIVA;
        repository.persist(s);
        LOG.infof("Sessão criada: sessaoId=%d", s.id);
        return s;
    }

    public Sessao criarNovaSessao() {
        Sessao s = new Sessao();
        s.data = LocalDate.now(ZONA);
        s.status = Sessao.Status.ATIVA;
        repository.persist(s);
        return s;
    }

    public PartidaResponseDTO estadoTimesSessaoAtual(Long sessaoId) {
        LOG.infof("Consultando estado dos times: sessaoId=%d", sessaoId);
        List<JogadorSessao> ladoA = jogadorRepository.emCampo(sessaoId, LadoTime.A);
        List<JogadorSessao> ladoB = jogadorRepository.emCampo(sessaoId, LadoTime.B);
        List<JogadorSessao> fila = jogadorRepository.naFila(sessaoId);

        PartidaResponseDTO estado = PartidaResponseDTO.from(
                new Sorteador.Times(sessaoId, ladoA, ladoB), fila);
        LOG.infof("Estado dos times consultado: sessaoId=%d ladoA=%d ladoB=%d fila=%d",
                sessaoId, ladoA.size(), ladoB.size(), fila.size());
        return estado;
    }

    public Sessao buscarAtiva() {
        LOG.info("Consultando sessão ativa");
        Sessao sessao = repository.buscarAtiva()
                .orElseThrow(() -> new NotFoundException("Nenhuma sessão ativa"));
        LOG.infof("Sessão ativa consultada: sessaoId=%d", sessao.id);
        return sessao;
    }

    public Sessao buscarSessaoAtivaPorId(Long sessaoId) {
        return repository.findByIdOptional(sessaoId)
                .filter(s -> s.status == Sessao.Status.ATIVA)
                .orElseThrow(() -> new NotFoundException("Sessão ativa não encontrada"));
    }

    private boolean expirada(Sessao s) {
        return Duration.between(
                s.criadoEm, LocalDateTime.now()
        ).compareTo(LIMITE) > 0;
    }

}