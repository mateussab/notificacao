package com.mateus.notificacao.business;

import com.mateus.notificacao.business.dto.TarefasDTO;
import com.mateus.notificacao.infrastructure.exceptions.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${envio.email.remetente}")
    public String remetente;

    @Value("${envio.email.nomeRemetente}")
    private String nomeRemetente;

    public void enviaEmail(TarefasDTO tarefaDTO){
        try{
            MimeMessage mensagem = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, StandardCharsets.UTF_8.name());

            helper.setFrom(new InternetAddress(remetente,nomeRemetente));
            helper.setTo(InternetAddress.parse(tarefaDTO.getEmailUsuario()));
            helper.setSubject("Notificação de tarefa");

            Context context = new Context();
            context.setVariable("nomeTarefa",tarefaDTO.getNomeTarefa());
            context.setVariable("dataEvento",tarefaDTO.getDataEvento());
            context.setVariable("descricao",tarefaDTO.getDescricao());
            String template = templateEngine.process("notificacao", context);

            helper.setText(template,true);
            javaMailSender.send(mensagem);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Erro ao enviar o email", e.getCause());
        }
    }







}
