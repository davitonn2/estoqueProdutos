package estoque.com.project.Services;

import estoque.com.project.Models.Produto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

@Service
@Slf4j
public class NotificacaoService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetenteEmail;

    public NotificacaoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarAlertaEstoqueBaixo(Produto produto) {
        String assunto = "[ALERTA DE ESTOQUE BAIXO] Produto: " + produto.getNome();
        String mensagem = String.format("O produto '%s' (ID: %d) está com baixo estoque. Quantidade atual: %d. " +
                        "Recomendamos fazer um novo pedido ao fornecedor %s.",
                produto.getNome(), produto.getId(), produto.getQuantidadeEstoque(), produto.getFornecedor().getNome());

        log.warn("ALERTA DE ESTOQUE BAIXO: {}", mensagem);

        try {
            sendEmail(remetenteEmail, "estoque.responsavel@empresa.com.br", assunto, mensagem);
            log.info("E-mail de alerta de estoque baixo enviado para {}", "estoque.responsavel@empresa.com.br");
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Falha ao enviar e-mail de alerta de estoque para o produto {}: {}", produto.getNome(), e.getMessage());
        }
    }

    private void sendEmail(String from, String to, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        if (Objects.equals(remetenteEmail, "seuemail@gmail.com") || remetenteEmail == null || remetenteEmail.isEmpty()) {
            log.warn("Configurações de e-mail incompletas ou padrão. E-mail de alerta não enviado para: {}", to);
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(from, "Sistema de Estoque");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, false);

        mailSender.send(message);
    }
}