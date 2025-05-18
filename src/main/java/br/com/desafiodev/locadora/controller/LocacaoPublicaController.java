package br.com.desafiodev.locadora.controller;

import br.com.desafiodev.locadora.model.Locacao;
import br.com.desafiodev.locadora.service.LocacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/publico")
public class LocacaoPublicaController {

    @Autowired
    private LocacaoService locacaoService;


     // Exibe as locações pendentes (não devolvidas) com base no CPF informado
    @GetMapping("/consulta")
    public String listarPorCpf(@RequestParam String cpf, Model model) {
        List<Locacao> locacoes = locacaoService.buscarPendentesPorCpf(cpf);

        model.addAttribute("cpf", cpf);
        model.addAttribute("locacoes", locacoes);

        return "locacoes-publicas";
    }

    // Exibe o formulário para que o usuário digite o CPF
    @GetMapping("/form")
    public String exibirFormularioConsulta() {
        return "consulta-cpf";
    }
}
