package br.com.desafiodev.locadora.controller;

import br.com.desafiodev.locadora.model.Exemplar;
import br.com.desafiodev.locadora.service.ExemplarService;
import br.com.desafiodev.locadora.repository.FilmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/exemplares")
public class ExemplarController {

    @Autowired
    private ExemplarService exemplarService;

    @Autowired
    private FilmeRepository filmeRepository;

    // Exibe o formulário para cadastrar novos exemplares
    @GetMapping("/novo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("filmes", filmeRepository.findByAtivoTrue()); // Carrega os filmes ativos para o select do formulário
        return "form-exemplar";
    }

    // Salva os exemplares no banco após o formulário ser submetido
    @PostMapping("/salvar")
    public String salvarExemplares(
            @RequestParam Long filmeId,
            @RequestParam int quantidade,
            @RequestParam(required = false) Boolean ativo,
            Model model
    ) {
        try {
            exemplarService.adicionarExemplares(filmeId, quantidade, ativo != null && ativo); // Chama o service para criar os exemplares
            return "redirect:/exemplares/novo"; // Redireciona para o mesmo formulário
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("filmes", filmeRepository.findByAtivoTrue());
            return "exemplar-form";
        }
    }

    // Exibe o formulário de edição de um exemplar específico
    @GetMapping("/editar/{id}")
    public String editarExemplar(@PathVariable Long id, Model model) {
        Exemplar exemplar = exemplarService.buscarPorId(id); // Busca o exemplar pelo ID

        // Verifica se o exemplar está alugado (alguma locação ainda sem dataDevolvido)
        boolean temLocacaoPendente = exemplar.getLocacoesExemplares().stream()
                .anyMatch(locEx -> locEx.getLocacao().getDataDevolvido() == null);

        // Adiciona os dados ao modelo para exibição no HTML
        model.addAttribute("exemplar", exemplar);
        model.addAttribute("filmes", filmeRepository.findByAtivoTrue());
        model.addAttribute("temLocacaoPendente", temLocacaoPendente); // Usado no HTML para desabilitar o checkbox

        return "form-exemplar"; // Reaproveita o mesmo formulário de criação para edição
    }

    // Atualiza o status de um exemplar (ativo/inativo)
    @PostMapping("/atualizar/{id}")
    public String atualizarExemplar(@PathVariable Long id,
                                    @RequestParam(required = false) Boolean ativo,
                                    Model model) {
        try {
            exemplarService.atualizarExemplar(id, ativo != null && ativo); // Atualiza o status no service
            return "redirect:/exemplares/listar"; // Após atualizar, redireciona para a lista de exemplares
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("exemplar", exemplarService.buscarPorId(id));
            model.addAttribute("filmes", filmeRepository.findByAtivoTrue());
            return "lista-exemplares";
        }
    }

    // Lista todos os exemplares
    @GetMapping("/listar")
    public String listarExemplares(Model model) {
        List<Exemplar> exemplares = exemplarService.listarTodos(); // Chama o service para buscar todos os exemplares
        model.addAttribute("exemplares", exemplares);
        return "lista-exemplares";
    }

}
