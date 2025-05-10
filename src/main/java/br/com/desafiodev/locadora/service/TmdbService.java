package br.com.desafiodev.locadora.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.desafiodev.locadora.model.Filme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TmdbService {

    private static final Logger logger = LoggerFactory.getLogger(TmdbService.class);

    // Injeta o valor do  - valor em .env
    @Value("${api.token.v4}")
    private String apiTokenV4;
    private final String urlDiscoverMovie = "https://api.themoviedb.org/3/discover/movie?language=pt-BR";


    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();


    // Autenticação V4 para poder acessar o endpoint de V3
    public Filme buscarFilmeAleatorio() {
        try {
            logger.info("Iniciando busca de filme aleatório com autenticação V4...");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiTokenV4);
            headers.set("Content-Type", "application/json;charset=utf-8");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(urlDiscoverMovie, HttpMethod.GET, entity, String.class);
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode
             results = root.path("results");

            return processarFilme(results);
        } catch (Exception e) {
            logger.error("Erro ao buscar filme usando token da API V4", e);
            throw new RuntimeException("Erro ao buscar filme usando token da API V4", e);
        }
    }


    // Método comum para processar os filmes
    private Filme processarFilme(JsonNode results) {
        int totalFilmes = results.size();
        if (totalFilmes == 0) {
            logger.warn("Nenhum filme encontrado.");
            return null;
        }

        // Escolhe um filme aleatório
        JsonNode escolhido = results.get(new Random().nextInt(totalFilmes));
        Filme filme = new Filme();
        filme.setTitulo(escolhido.path("title").asText());
        filme.setResumo(escolhido.path("overview").asText());
        filme.setPontuacao(escolhido.path("vote_average").asText());

        String dataLancamento = escolhido.path("release_date").asText();
        if (!dataLancamento.isEmpty()) {
            filme.setLancamento(dataLancamento);
        }

        logger.info("Filme escolhido: {}", filme.getTitulo());
        return filme;
    }
}