package service.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import service.auth.entity.Asunto;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${MAIL_USERNAME}")
    private String remitente;

    private final JavaMailSender mailSender;

    @Async
    public void enviarCorreoVerificacion(String email, String linkVerificacion){
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(email);
        mensaje.setFrom(remitente);
        mensaje.setSubject(Asunto.VERIFICACION.getDescripcion());
        mensaje.setText(linkVerificacion);
        mailSender.send(mensaje);
    }

}
