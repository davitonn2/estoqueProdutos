package estoque.com.project.Services;

import estoque.com.project.Exceptions.BusinessException;
import estoque.com.project.Exceptions.ResourceNotFoundException;
import estoque.com.project.Models.Pedido;
import estoque.com.project.Models.Produto;
import estoque.com.project.Repositories.PedidoRepository;
import estoque.com.project.Repositories.ProdutoRepository;
import estoque.com.project.Utils.GeradorRelatorioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public byte[] gerarRelatorioVendasPDF(LocalDate dataInicio, LocalDate dataFim) throws IOException {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);

        List<Pedido> pedidos = pedidoRepository.findPedidosBetweenDates(inicio, fim);

        if (pedidos.isEmpty()) {
            throw new BusinessException("Não há pedidos registrados no período especificado.");
        }

        // Prepara os dados para o relatório de PDF
        StringBuilder content = new StringBuilder();
        content.append("<h1>Relatório de Vendas</h1>");
        content.append("<p>Período: ").append(dataInicio).append(" a ").append(dataFim).append("</p>");
        content.append("<table border='1' cellpadding='5' cellspacing='0' style='width:100%;'>")
                .append("<thead><tr><th>ID Pedido</th><th>Data</th><th>Valor Total</th><th>Status</th><th>Itens</th></tr></thead>")
                .append("<tbody>");

        BigDecimal totalVendasGeral = BigDecimal.ZERO;

        for (Pedido pedido : pedidos) {
            content.append("<tr>")
                    .append("<td>").append(pedido.getId()).append("</td>")
                    .append("<td>").append(pedido.getDataPedido()).append("</td>")
                    .append("<td>R$ ").append(pedido.getValorTotal()).append("</td>")
                    .append("<td>").append(pedido.getStatus()).append("</td>")
                    .append("<td><ul>");
            pedido.getItens().forEach(item ->
                    content.append("<li>").append(item.getProduto().getNome())
                            .append(" (").append(item.getQuantidade()).append("x ")
                            .append("R$ ").append(item.getPrecoUnitario()).append(")</li>")
            );
            content.append("</ul></td>")
                    .append("</tr>");
            totalVendasGeral = totalVendasGeral.add(pedido.getValorTotal());
        }
        content.append("</tbody></table>");
        content.append("<h2>Total de Vendas no Período: R$ ").append(totalVendasGeral).append("</h2>");

        return GeradorRelatorioUtil.gerarPdfDeHtml(content.toString());
    }

    @Transactional(readOnly = true)
    public byte[] gerarRelatorioProdutosMaisVendidosCSV() {
        List<Object[]> resultados = produtoRepository.findProdutosMaisVendidos();

        if (resultados.isEmpty()) {
            throw new BusinessException("Não há dados de produtos mais vendidos disponíveis.");
        }

        StringBuilder csvContent = new StringBuilder();
        csvContent.append("ID do Produto,Nome do Produto,Total Vendido\n");

        for (Object[] row : resultados) {
            Produto produto = (Produto) row[0];
            Long totalVendido = (Long) row[1];
            csvContent.append(produto.getId()).append(",")
                    .append("\"").append(produto.getNome().replace("\"", "\"\"")).append("\",")
                    .append(totalVendido)
                    .append("\n");
        }
        return csvContent.toString().getBytes();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> gerarAnaliseTendenciasEstoque() {

        log.warn("ATENÇÃO: A análise de tendências de estoque é uma simulação. Para dados reais, implemente uma entidade de MovimentacaoEstoque.");

        List<Produto> produtos = produtoRepository.findAll();

        return produtos.stream().map(produto -> {
            Map<String, Object> tendencia = new LinkedHashMap<>();
            tendencia.put("idProduto", produto.getId());
            tendencia.put("nomeProduto", produto.getNome());
            tendencia.put("quantidadeAtual", produto.getQuantidadeEstoque());
            tendencia.put("dataUltimaAtualizacao", produto.getDataUltimaAtualizacao());

            if (produto.getQuantidadeEstoque() < 5) {
                tendencia.put("tendencia", "Alerta: Baixo Estoque. Reabastecer!");
            } else if (produto.getQuantidadeEstoque() > 50 && produto.getDataUltimaAtualizacao().isAfter(LocalDateTime.now().minusDays(7))) {
                tendencia.put("tendencia", "Estável/Aumentando (recentemente reposto ou alta disponibilidade)");
            } else {
                tendencia.put("tendencia", "Estável");
            }

            return tendencia;
        }).collect(Collectors.toList());
    }


    // Método auxiliar para obter a entidade Produto (se necessário)
    @Transactional(readOnly = true)
    public Produto getProdutoEntity(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
    }
}