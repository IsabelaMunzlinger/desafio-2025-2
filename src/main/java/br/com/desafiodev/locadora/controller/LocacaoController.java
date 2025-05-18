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

    // Mostra a tela de cadastrado para uma nova locação
    @GetMapping("/novo")
    public String novaLocacaoForm(Model model) {
        model.addAttribute("locacao", new Locacao());
        //Filtrar apenas exemplares válidos: ativos e não alugados
        List<Exemplar> exemplaresDisponiveis = exemplarRepository.buscarExemplaresDisponiveisParaLocacao();
        model.addAttribute("exemplaresDisponiveis", exemplaresDisponiveis);

        return "form-locacao";
    }

    // Salva a locação (ao submeter o formulário de criação de locação)
    @PostMapping("/salvar")
    public String salvarLocacao(@Valid @ModelAttribute("locacao") br.com.desafiodev.locadora.dto.LocacaoDTO dto,
                                BindingResult result,
                                Model model) {
        // Verifica se houve erros de validação no formulário
        if (result.hasErrors()) {
            // Se sim, recarrega os exemplares disponíveis e retorna o formulário novamente
            model.addAttribute("exemplaresDisponiveis", exemplarRepository.buscarExemplaresDisponiveisParaLocacao());
            return "form-locacao"; // Volta para a tela do formulário com os erros exibidos
        }

        try {
            // Chama a service para salvar a locação
            locacaoService.salvarLocacao(dto);

            // Se tudo der certo, redireciona para a listagem de locações
            return "redirect:/locacoes";
        } catch (IllegalArgumentException ex) {
            result.reject(null, ex.getMessage());

            // Recarrega os exemplares disponíveis e retorna para o formulário com a mensagem de erro
            model.addAttribute("exemplaresDisponiveis", exemplarService.listarExemplaresDisponiveisParaLocacao());
            return "form-locacao";
        }
    }

}