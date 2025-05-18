package br.com.desafiodev.locadora.controller;

import br.com.desafiodev.locadora.model.Exemplar;
import br.com.desafiodev.locadora.model.Locacao;
import br.com.desafiodev.locadora.repository.ExemplarRepository;
import br.com.desafiodev.locadora.service.ExemplarService;
import br.com.desafiodev.locadora.service.FilmeService;
import br.com.desafiodev.locadora.service.LocacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
// Rota base para todas as requisições relacionadas a locações
@RequestMapping("/locacoes")
public class LocacaoController {

    @Autowired
    private ExemplarService exemplarService;

    @Autowired
    private ExemplarRepository exemplarRepository;

    @Autowired
    private FilmeService filmeService;

    @Autowired
    private LocacaoService locacaoService;

    // Cria uma nova locação
    @GetMapping("/novo")
    public String novaLocacaoForm(Model model) {
        model.addAttribute("locacao", new Locacao());

        // Busca apenas exemplares disponíveis para locação (ativos e não alugados)
        List<Exemplar> exemplaresDisponiveis = exemplarRepository.buscarExemplaresDisponiveisParaLocacao();
        model.addAttribute("exemplaresDisponiveis", exemplaresDisponiveis);

        return "form-locacao";
    }

    // Processa o envio do formulário de criação de locação
    @PostMapping("/salvar")
    public String salvarLocacao(
            @Valid @ModelAttribute("locacao") br.com.desafiodev.locadora.dto.LocacaoDTO dto,
            BindingResult result,
            Model model) {

        // Se houver erros de validação no DTO, retorna ao formulário com os erros
        if (result.hasErrors()) {
            model.addAttribute("exemplaresDisponiveis", exemplarRepository.buscarExemplaresDisponiveisParaLocacao());
            return "form-locacao";
        }

        try {
            // Tenta salvar a locação usando a service
            locacaoService.salvarLocacao(dto);

            // Redireciona para a lista de locações se tudo ocorrer bem
            return "redirect:/locacoes/listar";
        } catch (IllegalArgumentException ex) {
            // Em caso de erro de regra de negócio, adiciona mensagem de erro
            result.reject(null, ex.getMessage());

            // Recarrega exemplares e retorna ao formulário
            model.addAttribute("exemplaresDisponiveis", exemplarService.listarExemplaresDisponiveisParaLocacao());
            return "form-locacao";
        }
    }

    // Realiza a devolução de uma locação com o ID informado
    @PostMapping("/{id}/devolver")
    public String devolverLocacao(@PathVariable Long id) {
        locacaoService.devolverLocacao(id); // Marca a locação como devolvida
        return "redirect:/locacoes/listar"; // Redireciona para a lista de locações
    }

    // Lista todas as locações, com filtros opcionais (nome, CPF, email, status)
    @GetMapping("/listar")
    public String listarLocacoes(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            Model model) {

        // Busca locações filtradas com base nos parâmetros fornecidos
        List<Locacao> locacoes = locacaoService.buscarFiltradas(nome, cpf, status, email);
        model.addAttribute("locacoes", locacoes);
        return "lista-locacoes";
    }

    // Exibe o QR Code gerado para uma locação específica
    @GetMapping("/{id}/qrcode")
    public String exibirQrCode(@PathVariable Long id, Model model) {
        // Busca a locação pelo ID ou lança exceção se não encontrada
        Locacao locacao = locacaoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Locação não encontrada com ID: " + id));

        // Adiciona o QR code (em base64) ao modelo para ser exibido
        model.addAttribute("qrCodeBase64", locacao.getQrCode());
        return "detalhesLocacao";
    }
}
