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

import java.time.Year;

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

        String mensaje = switch (asunto) {
            case VERIFICACION -> "¡Bienvenido a RealtyHub! Estás a un paso de empezar a gestionar tus propiedades, clientes y oportunidades en un solo lugar. Confirma tu correo para activar tu cuenta.";
            case RESTABLECER_ACCESO -> "Recibimos una solicitud para restablecer la contraseña de tu cuenta en RealtyHub. Haz clic en el botón para crear una nueva y recuperar el acceso a tu panel.";
        };

        int anion = Year.now().getValue();

        return """
        <!DOCTYPE html>
        <html lang="es">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>RealtyHub</title>
        </head>
        <body style="margin:0; padding:0; background-color:#fcf7f0; font-family:Arial, Helvetica, sans-serif;">
          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" bgcolor="#fcf7f0">
            <tr>
              <td align="center" style="padding:32px 16px;">
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0"
                       style="max-width:560px; background-color:#ffffff; border:1px solid #d8c2a4; border-radius:10px; overflow:hidden;">

                  <!-- Encabezado -->
                  <tr>
                    <td bgcolor="#0a3b35" style="padding:24px 32px; border-bottom:4px solid #d8c2a4;">
                      <span style="font-size:22px; font-weight:bold; color:#fcf7f0; letter-spacing:0.5px;">Realty<span style="color:#d8c2a4;">Hub</span></span><br>
                      <span style="font-size:12px; color:#b2b7aa;">Gestión inmobiliaria y CRM</span>
                    </td>
                  </tr>

                  <!-- Contenido -->
                  <tr>
                    <td style="padding:32px;">
                      <h2 style="margin:0 0 16px; font-size:20px; color:#0a3b35;">%2$s</h2>
                      <p style="margin:0 0 24px; font-size:15px; line-height:1.6; color:#2a6151;">%3$s</p>

                      <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                        <tr>
                          <td bgcolor="#2a6151" style="border-radius:6px;">
                            <a href="%1$s" target="_blank"
                               style="display:inline-block; padding:14px 28px; font-size:15px; font-weight:bold;
                                      color:#fcf7f0; text-decoration:none; border-radius:6px;">
                              %4$s
                            </a>
                          </td>
                        </tr>
                      </table>

                      <p style="margin:28px 0 8px; font-size:13px; color:#555555;">
                        Si el botón no funciona, copia y pega este enlace en tu navegador:
                      </p>
                      <p style="margin:0; font-size:13px; word-break:break-all;">
                        <a href="%1$s" target="_blank" style="color:#2a6151; text-decoration:underline;">%1$s</a>
                      </p>
                    </td>
                  </tr>

                  <!-- Aviso -->
                  <tr>
                    <td style="padding:16px 32px; background-color:#fcf7f0; border-top:1px solid #d8c2a4;">
                      <p style="margin:0; font-size:12px; line-height:1.5; color:#777777;">
                        Si no solicitaste esta acción, puedes ignorar este correo con tranquilidad; tu cuenta seguirá segura.
                      </p>
                    </td>
                  </tr>
                </table>

                <p style="margin:16px 0 0; font-size:11px; color:#777777;">
                  © %5$d RealtyHub · Todos los derechos reservados
                </p>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """.formatted(url, asunto.getDescripcion(), mensaje, textoBoton, anion);
    }
}
