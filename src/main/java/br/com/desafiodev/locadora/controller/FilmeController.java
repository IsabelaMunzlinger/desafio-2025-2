package br.com.desafiodev.locadora.controller;

import br.com.desafiodev.locadora.dto.FilmeDTO;
import br.com.desafiodev.locadora.model.Filme;
import br.com.desafiodev.locadora.repository.FilmeRepository;
import br.com.desafiodev.locadora.service.FilmeService;
import br.com.desafiodev.locadora.service.TmdbService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;


@Controller
@RequestMapping("/filmes")
public class FilmeController {

    @Autowired
    private FilmeService filmeService;

    @Autowired
    private TmdbService tmdbService;

    @Autowired
    FilmeRepository filmeRepository;

    // Exibe formulário vazio
    @GetMapping("/form")
    public String exibirFormulario(Model model) {
        model.addAttribute("filme", new Filme());
        return "form-filme";
    }

    // Exibe formulário com dados aleatórios do endpoint /discover/movie
    @GetMapping("/buscar")
    public String buscarFilmeAleatorio(Model model) {
        Filme filme = tmdbService.buscarFilmeAleatorio();
        model.addAttribute("filme", filme);
        return "form-filme";
    }

    // Retorna que o filme foi salvo
    @PostMapping("/salvar")
    public String salvarFilme(@ModelAttribute FilmeDTO filmeDTO, Model model, BindingResult result) {
        if (result.hasErrors()) {
            return "form-filme";
        }
        try {
            filmeService.salvarFilme(filmeDTO);
            return "redirect:/filmes/form?salvo=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("filme", filmeDTO); // mantém os dados preenchidos
            model.addAttribute("erro", e.getMessage());
            return "form-filme"; // volta para o formulário do filme
        }
    }

    // Mostra o formulário para edição do filme
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model) {
        Optional<Filme> filmeOpt = filmeRepository.findById(id);
        if (filmeOpt.isPresent()) {
            model.addAttribute("filme", filmeOpt.get());
            return "editar-filme";
        } else {
            return "redirect:/filmes/listar"; // redireciona para a lista de filmes
        }
    }


    // Salva as edições feitas em um filme
    @PostMapping("/editar/{id}")
    public String salvarEdicaoFilme(@PathVariable Long id, @ModelAttribute Filme filmeEditado) {
        Optional<Filme> filmeOriginalOpt = filmeRepository.findById(id);
        if (filmeOriginalOpt.isPresent()) {
            Filme filmeOriginal = filmeOriginalOpt.get();

            // Só edita os campos que o usuário pode alterar
            filmeOriginal.setExemplaresDisponiveis(filmeEditado.getExemplaresDisponiveis());   // Exemplares disponíveis
            filmeOriginal.setAtivo(filmeEditado.isAtivo()); // Se o filme está ativo/inativo

            filmeRepository.save(filmeOriginal);
        }

        return "redirect:/filmes/listar";
    }

    // Lista todos os filmes
    @GetMapping("/listar")
    public String listarFilmes(Model model) {
        model.addAttribute("filmes", filmeRepository.findAll());
        return "lista-filmes";
    }

}



