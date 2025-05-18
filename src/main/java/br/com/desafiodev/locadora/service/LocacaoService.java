package br.com.desafiodev.locadora.service;

import br.com.desafiodev.locadora.model.Exemplar;
import br.com.desafiodev.locadora.model.Filme;
import br.com.desafiodev.locadora.model.Locacao;
import br.com.desafiodev.locadora.model.LocacaoExemplar;
import br.com.desafiodev.locadora.repository.ExemplarRepository;
import br.com.desafiodev.locadora.repository.FilmeRepository;
import br.com.desafiodev.locadora.repository.LocacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocacaoService {

    @Autowired
    LocacaoRepository locacaoRepository;

    @Autowired
    ExemplarRepository exemplarRepository;

    @Autowired
    FilmeRepository filmeRepository;

    // Salva uma nova locação a partir de um DTO
    @Transactional
    public void salvarLocacao(br.com.desafiodev.locadora.dto.LocacaoDTO dto) {
        Locacao locacao = new Locacao();
        locacao.setNome(dto.getNome());
        locacao.setCpf(dto.getCpf());
        locacao.setEmail(dto.getEmail());
        locacao.setTelefone(dto.getTelefone());
        locacao.setDataLocacao(dto.getDataLocacao());
        locacao.setDataDevolucao(dto.getDataDevolucao());

        this.salvarLocacao(locacao, dto.getExemplarIds());
    }

    // Salva a locação e atualiza os exemplares e filmes envolvidos
    @Transactional
    public void salvarLocacao(Locacao locacao, List<Long> exemplarIds) {
        if (exemplarIds.size() > 3) {
            throw new IllegalArgumentException("Você pode alugar no máximo 3 filmes por locação.");
        }

        List<LocacaoExemplar> locacoesExemplares = new ArrayList<>();
        Set<Filme> filmesAtualizados = new HashSet<>();

        for (Long exemplarId : exemplarIds) {
            Exemplar exemplar = exemplarRepository.findById(exemplarId)
                    .orElseThrow(() -> new IllegalArgumentException("Exemplar com ID " + exemplarId + " não encontrado."));

            if (!exemplar.isAtivo()) {
                throw new IllegalArgumentException("Exemplar com ID " + exemplarId + " está inativo.");
            }

            Filme filme = exemplar.getFilme();

            if (filme == null || !filme.isAtivo()) {
                throw new IllegalArgumentException("Filme do exemplar com ID " + exemplarId + " está inativo ou nulo.");
            }

            if (filme.getExemplaresDisponiveis() < 1) {
                throw new IllegalArgumentException("Filme '" + filme.getTitulo() + "' não possui exemplares disponíveis.");
            }

            // Relaciona exemplar com locação
            LocacaoExemplar locacaoExemplar = new LocacaoExemplar();
            locacaoExemplar.setLocacao(locacao);
            locacaoExemplar.setExemplar(exemplar);
            locacoesExemplares.add(locacaoExemplar);

            // Atualiza quantidade disponível
            filme.setExemplaresDisponiveis(filme.getExemplaresDisponiveis() - 1);
            filmesAtualizados.add(filme);

            // Inativa exemplar alugado
            exemplar.setAtivo(false);
            exemplarRepository.save(exemplar);
        }

        // Salva os filmes com nova disponibilidade
        filmeRepository.saveAll(filmesAtualizados);

        // Gera o QR Code para a locação
        String qrCodeBase64 = gerarQRCodeViaApi(locacao);
        locacao.setQrCode(qrCodeBase64);

        // Associa exemplares à locação
        locacao.setLocacoesExemplares(locacoesExemplares);

        // Salva locação final
        locacaoRepository.save(locacao);
    }

    // Gera QR Code com dados da locação via API externa
    private String gerarQRCodeViaApi(Locacao locacao) {
        try {
            String json = gerarJsonParaQrCode(locacao);
            String urlEncodedData = URLEncoder.encode(json, StandardCharsets.UTF_8);
            String url = "https://api.apgy.in/qr/?data=" + urlEncodedData + "&size=150"; //Tamanho reduzido para ocupar menos espaço

            RestTemplate restTemplate = new RestTemplate();
            byte[] imageBytes = restTemplate.getForObject(url, byte[].class);
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao gerar QR Code via API externa", e);
        }
    }

    // Gera JSON com dados da locação para o QR Code
    private String gerarJsonParaQrCode(Locacao locacao) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return """
        {
            "cpf": "%s",
            "telefone": "%s",
            "dataLocacao": "%s",
            "dataDevolucao": "%s"
        }
        """.formatted(
                locacao.getCpf(),
                locacao.getTelefone(),
                locacao.getDataLocacao().format(formatter),
                locacao.getDataDevolucao().format(formatter)
        );
    }

    // Retorna todas as locações
    public List<Locacao> listarTodas() {
        return locacaoRepository.findAll();
    }

    // Processa a devolução de uma locação
    @Transactional
    public void devolverLocacao(Long locacaoId) {
        Locacao locacao = locacaoRepository.findById(locacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Locação não encontrada"));

        if (locacao.getDataDevolvido() != null) {
            throw new IllegalStateException("Locação já foi devolvida.");
        }

        locacao.setDataDevolvido(new Date());

        for (LocacaoExemplar locExemplar : locacao.getLocacoesExemplares()) {
            Exemplar exemplar = locExemplar.getExemplar();
            exemplar.setAtivo(true);

            Filme filme = exemplar.getFilme();
            Long disponiveis = filme.getExemplaresDisponiveis() != null ? filme.getExemplaresDisponiveis() : 0L;
            filme.setExemplaresDisponiveis(disponiveis + 1);

            exemplarRepository.save(exemplar);
            filmeRepository.save(filme);
        }

        locacaoRepository.save(locacao);
    }

    // Busca locações com filtros opcionais
    public List<Locacao> buscarFiltradas(String nome, String cpf, String status, String email) {
        return locacaoRepository.findAll().stream()
                .filter(l -> nome == null || nome.isBlank() || l.getNome().toLowerCase().contains(nome.toLowerCase()))
                .filter(l -> cpf == null || cpf.isBlank() || l.getCpf().contains(cpf))
                .filter(l -> email == null || email.isBlank() || l.getEmail().contains(email))
                .filter(l -> {
                    if ("pendente".equalsIgnoreCase(status)) return l.getDataDevolvido() == null;
                    if ("devolvida".equalsIgnoreCase(status)) return l.getDataDevolvido() != null;
                    return true;
                })
                .collect(Collectors.toList());
    }

    // Retorna locações pendentes por CPF
    public List<Locacao> buscarPendentesPorCpf(String cpf) {
        return locacaoRepository.findAll().stream()
                .filter(l -> l.getCpf().equalsIgnoreCase(cpf))
                .filter(l -> l.getDataDevolvido() == null)
                .collect(Collectors.toList());
    }

    // Retorna locação opcional por ID
    public Optional<Locacao> buscarPorId(Long id) {
        return locacaoRepository.findById(id);
    }
}

