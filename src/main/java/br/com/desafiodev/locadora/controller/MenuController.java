package br.com.desafiodev.locadora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/menu")
    public String mostrarMenu() {
        return "form-menu"; // Vai buscar o menu.html
    }
}
