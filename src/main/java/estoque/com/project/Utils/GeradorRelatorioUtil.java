package estoque.com.project.Utils;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class GeradorRelatorioUtil {

    private GeradorRelatorioUtil() {
        // Utility class
    }

    public static byte[] gerarPdfDeHtml(String htmlContent) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(byteArrayOutputStream)) {
            HtmlConverter.convertToPdf(htmlContent, writer);
            log.info("PDF gerado a partir de HTML com sucesso.");
        } catch (IOException e) {
            log.error("Erro ao gerar PDF a partir de HTML: {}", e.getMessage(), e);
            throw new IOException("Falha ao gerar relatório PDF.", e);
        }
        return byteArrayOutputStream.toByteArray();
    }
}