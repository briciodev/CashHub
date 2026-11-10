package br.com.cashhub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @GetMapping
    public Map<String, List<String>> listarCategorias() {

        List<String> receitas = List.of(
                "Salário",
                "Freelance",
                "Vendas",
                "Rendimentos",
                "Outros"
        );

        List<String> despesas = List.of(
                "Moradia",
                "Alimentação",
                "Transporte",
                "Saúde",
                "Educação",
                "Lazer",
                "Assinaturas",
                "Compras",
                "Contas",
                "Outros"
        );

        return Map.of(
                "receitas", receitas,
                "despesas", despesas
        );
    }
}