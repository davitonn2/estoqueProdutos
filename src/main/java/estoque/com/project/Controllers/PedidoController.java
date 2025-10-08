package estoque.com.project.Controllers;

import estoque.com.project.DTO.PedidoRequestDTO;
import estoque.com.project.DTO.PedidoResponseDTO;
import estoque.com.project.Models.StatusPedido;
import estoque.com.project.Services.PedidoService;
import estoque.com.project.Services.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "API para gerenciamento de pedidos de venda")
public class PedidoController {

    private final PedidoService pedidoService;
    private final RelatorioService relatorioService;

    @Operation(summary = "Lista todos os pedidos", description = "Retorna uma lista de todos os pedidos registrados")
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> findAll() {
        List<PedidoResponseDTO> pedidos = pedidoService.findAll();
        return ResponseEntity.ok(pedidos);
    }

    @Operation(summary = "Busca um pedido por ID", description = "Retorna um pedido específico pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> findById(@PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.findById(id);
        return ResponseEntity.ok(pedido);
    }

    @Operation(summary = "Cria um novo pedido", description = "Registra um novo pedido no sistema, verificando e diminuindo o estoque dos produtos")
    @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou estoque insuficiente")
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> create(@Valid @RequestBody PedidoRequestDTO pedidoDTO) {
        PedidoResponseDTO novoPedido = pedidoService.create(pedidoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    @Operation(summary = "Atualiza o status de um pedido", description = "Muda o status de um pedido existente (PENDENTE, CONCLUIDO, CANCELADO). Cancela o pedido e restaura o estoque.")
    @ApiResponse(responseCode = "200", description = "Status do pedido atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    @ApiResponse(responseCode = "400", description = "Transição de status inválida")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam @Parameter(description = "Novo status do pedido", example = "CONCLUIDO") StatusPedido novoStatus) {
        PedidoResponseDTO pedidoAtualizado = pedidoService.updateStatus(id, novoStatus);
        return ResponseEntity.ok(pedidoAtualizado);
    }

    @Operation(summary = "Exclui um pedido", description = "Remove um pedido do sistema pelo seu ID. Se o pedido não estiver concluído, o estoque é restaurado.")
    @ApiResponse(responseCode = "204", description = "Pedido excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Gera um relatório de vendas em PDF por período", description = "Retorna um relatório em PDF com o resumo das vendas em um período específico.")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou nenhum pedido no período")
    @GetMapping(value = "/relatorio/vendas/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> gerarRelatorioVendasPDF(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Data de início (YYYY-MM-DD)", example = "2023-01-01") LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "Data de fim (YYYY-MM-DD)", example = "2023-12-31") LocalDate dataFim) throws IOException {

        byte[] pdfContent = relatorioService.gerarRelatorioVendasPDF(dataInicio, dataFim);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "relatorio_vendas_" + dataInicio + "_a_" + dataFim + ".pdf";
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
}