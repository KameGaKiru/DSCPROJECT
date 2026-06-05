package br.edu.ifpe.dsc.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.edu.ifpe.dsc.model.FuncaoUsuario;
import br.edu.ifpe.dsc.model.MarcaVeiculo;
import br.edu.ifpe.dsc.model.TipoVeiculo;
import br.edu.ifpe.dsc.model.dto.Usuario;
import br.edu.ifpe.dsc.model.dto.Veiculo;
import br.edu.ifpe.dsc.model.repositorios.UsuarioRepositorio;
import br.edu.ifpe.dsc.model.repositorios.VeiculoRepositorio;

@TestConfiguration
public class TestDataConfig {

    @Bean
    CommandLineRunner carregarDadosTeste(
            UsuarioRepositorio usuarioRepositorio,
            VeiculoRepositorio veiculoRepositorio,
            PasswordEncoder passwordEncoder) {

        return args -> {

            criarUsuarioSeNaoExistir(
                    usuarioRepositorio,
                    passwordEncoder,
                    "111111",
                    "Motorista",
                    "Teste",
                    "motorista.teste@dsc.com",
                    "senha123",
                    FuncaoUsuario.MOTORISTA
            );

            criarUsuarioSeNaoExistir(
                    usuarioRepositorio,
                    passwordEncoder,
                    "222222",
                    "Coordenador",
                    "Teste",
                    "coordenador.teste@dsc.com",
                    "senha123",
                    FuncaoUsuario.COORDENADOR
            );

            criarUsuarioSeNaoExistir(
                    usuarioRepositorio,
                    passwordEncoder,
                    "333333",
                    "Mecanico",
                    "Teste",
                    "mecanico.teste@dsc.com",
                    "senha123",
                    FuncaoUsuario.MECANICO
            );

            if (veiculoRepositorio.findByNumero(999).isEmpty()) {
                Veiculo veiculo = new Veiculo();
                veiculo.setNumero(999);
                veiculo.setPlaca("TES-1234");
                veiculo.setMarca(MarcaVeiculo.FIAT);
                veiculo.setTipo(TipoVeiculo.VAN);
                veiculoRepositorio.save(veiculo);
            }
        };
    }

    private void criarUsuarioSeNaoExistir(
            UsuarioRepositorio usuarioRepositorio,
            PasswordEncoder passwordEncoder,
            String matricula,
            String nome,
            String sobrenome,
            String email,
            String senha,
            FuncaoUsuario funcao) {

        if (usuarioRepositorio.findByMatricula(matricula).isPresent()) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setMatricula(matricula);
        usuario.setNome(nome);
        usuario.setSobrenome(sobrenome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setFuncao(funcao);

        usuarioRepositorio.save(usuario);
    }
}