package br.edu.ifpe.dsc.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.dsc.model.Calculadora;
import br.edu.ifpe.dsc.model.dto.ResultadoDTO;

@RestController
@RequestMapping("/api/calculadora")
public class CalculadoraController {

    @Autowired
    private Calculadora calculadora;

    @GetMapping("soma/{a}/{b}")
    public ResultadoDTO calculeteSoma(@PathVariable Integer a, @PathVariable Integer b ){
       ResultadoDTO resultado = new ResultadoDTO();
       resultado.setOperacao("Soma"); ;
       resultado.setResultado(this.calculadora.somar(a,b));
       return resultado;
    }
    
    @GetMapping("subtrair/{a}/{b}")
    public ResultadoDTO calculeteSubtrair(@PathVariable Integer a, @PathVariable Integer b ){
       ResultadoDTO resultado = new ResultadoDTO();
       resultado.setOperacao("Subtrair"); ;
       resultado.setResultado(this.calculadora.subtrair(a,b));
       return resultado;
    }
    
    @GetMapping("multiplicar/{a}/{b}")
    public ResultadoDTO calculeteMultiplicar(@PathVariable Integer a, @PathVariable Integer b ){
       ResultadoDTO resultado = new ResultadoDTO();
       resultado.setOperacao("Multiplicar"); ;
       resultado.setResultado(this.calculadora.multiplicar(a,b));
       return resultado;
    }

    @GetMapping("dividir/{a}/{b}")
    public ResultadoDTO calculeteDividir(@PathVariable Integer a, @PathVariable Integer b ){
       ResultadoDTO resultado = new ResultadoDTO();
       resultado.setOperacao("Dividir"); ;
       resultado.setResultado(this.calculadora.dividir(a,b));
       return resultado;
    }
    
}
