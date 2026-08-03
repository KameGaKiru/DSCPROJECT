package br.edu.ifpe.dsc.controlador;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import br.edu.ifpe.dsc.model.dto.MensagemChat;
import br.edu.ifpe.dsc.model.dto.TipoMensagemChat;

@Controller
public class ChatControlador {

    @MessageMapping("/chat.enviar")
    @SendTo("/topic/chat")
    public MensagemChat enviar(MensagemChat mensagem) {

        validarMensagem(mensagem);

        mensagem.setConteudo(mensagem.getConteudo().trim());
        mensagem.setTipo(TipoMensagemChat.MENSAGEM);
        mensagem.setEnviadaEm(LocalDateTime.now());

        return mensagem;
    }

    @MessageMapping("/chat.entrar")
    @SendTo("/topic/chat")
    public MensagemChat entrar(MensagemChat mensagem) {

        mensagem.setTipo(TipoMensagemChat.ENTRADA);
        mensagem.setEnviadaEm(LocalDateTime.now());
        mensagem.setConteudo(
                mensagem.getRemetenteNome() + " entrou no chat."
        );

        return mensagem;
    }

    private void validarMensagem(MensagemChat mensagem) {

        if (mensagem == null) {
            throw new IllegalArgumentException("Mensagem inválida.");
        }

        if (mensagem.getRemetenteMatricula() == null
                || mensagem.getRemetenteMatricula().isBlank()) {
            throw new IllegalArgumentException("Remetente não informado.");
        }

        if (mensagem.getConteudo() == null
                || mensagem.getConteudo().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "A mensagem não pode estar vazia."
            );
        }

        if (mensagem.getConteudo().length() > 500) {
            throw new IllegalArgumentException(
                    "A mensagem deve possuir no máximo 500 caracteres."
            );
        }
    }
}