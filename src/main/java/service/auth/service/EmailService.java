package service.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import service.auth.entity.Asunto;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${MAIL_USERNAME}")
    private String remitente;

    private final JavaMailSender mailSender;

    @Async
    public void enviarCorreo(String email, String url, Asunto asunto) {

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");

            helper.setTo(email);
            helper.setFrom(remitente);
            helper.setSubject(asunto.getDescripcion());
            helper.setText(construirHtml(asunto, url), true);

            mailSender.send(mensaje);

        } catch (MessagingException e) {
            log.error("Error enviando correo de {} a {}: {}", asunto, email, e.getMessage());
        }
    }

    private String construirHtml(Asunto asunto, String url) {
        String textoBoton = switch (asunto) {
            case VERIFICACION -> "Verificar correo";
            case RESTABLECER_ACCESO -> "Restablecer contraseña";
        };

        return """
        <html>
        <body style="font-family: Arial, sans-serif;">
            <h2>%s</h2>
            <p>Haz clic en el siguiente botón para continuar:</p>
            <a href="%s"
               style="display:inline-block; padding:12px 24px; background-color:#2563eb;
                      color:#ffffff; text-decoration:none; border-radius:6px; font-weight:bold;">
                %s
            </a>
            <p style="color:#666; font-size:12px; margin-top:20px;">
                Si no solicitaste esta acción, ignora este correo.
            </p>
        </body>
        </html>
        """.formatted(asunto.getDescripcion(), url, textoBoton);
    }
}
