package br.com.desafiodev.locadora.controller;


import br.com.desafiodev.locadora.dto.FilmeDTO;
import br.com.desafiodev.locadora.model.Filme;
import br.com.desafiodev.locadora.service.FilmeService;
import br.com.desafiodev.locadora.service.TmdbService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/filmes")
public class FilmeController {

    @Autowired
    private FilmeService filmeService;

    @Autowired
    private TmdbService tmdbService;

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
            model.addAttribute("erro", e.getMessage());// passa mensagem de erro
            return "form-filme"; // volta para o formulário com erro
        }
    }

}



