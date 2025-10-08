package estoque.com.project.Controllers;

import estoque.com.project.DTO.ProdutoRequestDTO;
import estoque.com.project.DTO.ProdutoResponseDTO;
import estoque.com.project.Services.ProdutoService;
import estoque.com.project.Services.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "API para gerenciamento de produtos no estoque")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final RelatorioService relatorioService;

    @Operation(summary = "Lista todos os produtos", description = "Retorna uma lista de todos os produtos cadastrados")
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> findAll() {
        List<ProdutoResponseDTO> produtos = produtoService.findAll();
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Busca um produto por ID", description = "Retorna um produto específico pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> findById(@PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.findById(id);
        return ResponseEntity.ok(produto);
    }

    @Operation(summary = "Cadastra um novo produto", description = "Cria um novo produto no sistema")
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> create(@Valid @RequestBody ProdutoRequestDTO produtoDTO) {
        ProdutoResponseDTO novoProduto = produtoService.create(produtoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

    @Operation(summary = "Atualiza um produto existente", description = "Atualiza os dados de um produto pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO produtoDTO) {
        ProdutoResponseDTO produtoAtualizado = produtoService.update(id, produtoDTO);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @Operation(summary = "Exclui um produto", description = "Remove um produto do sistema pelo seu ID")
    @ApiResponse(responseCode = "204", description = "Produto excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        produtoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lista produtos por nome da categoria", description = "Retorna produtos filtrados por nome da categoria")
    @GetMapping("/por-categoria")
    public ResponseEntity<List<ProdutoResponseDTO>> findByCategoriaNome(
            @RequestParam @Parameter(description = "Nome da categoria para busca", example = "Eletrônicos") String nomeCategoria) {
        List<ProdutoResponseDTO> produtos = produtoService.findByCategoriaNome(nomeCategoria);
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Lista produtos por parte do nome", description = "Retorna produtos que contenham a string no nome (case-insensitive)")
    @GetMapping("/por-nome")
    public ResponseEntity<List<ProdutoResponseDTO>> findByNomeContaining(
            @RequestParam @Parameter(description = "Parte do nome do produto para busca", example = "fone") String nome) {
        List<ProdutoResponseDTO> produtos = produtoService.findByNomeContaining(nome);
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Lista produtos com baixo estoque", description = "Retorna produtos cuja quantidade em estoque está abaixo do limite definido (padrão: 5)")
    @GetMapping("/baixo-estoque")
    public ResponseEntity<List<ProdutoResponseDTO>> findProdutosComBaixoEstoque() {
        List<ProdutoResponseDTO> produtos = produtoService.findProdutosComBaixoEstoque();
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Gera um relatório CSV dos produtos mais vendidos", description = "Retorna um arquivo CSV com a lista dos produtos mais vendidos e a quantidade total vendida.")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    @ApiResponse(responseCode = "400", description = "Nenhum dado de vendas disponível")
    @GetMapping(value = "/relatorio/mais-vendidos/csv", produces = "text/csv")
    public ResponseEntity<byte[]> gerarRelatorioProdutosMaisVendidosCSV() {
        byte[] csvContent = relatorioService.gerarRelatorioProdutosMaisVendidosCSV();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "produtos_mais_vendidos.csv");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }

    @Operation(summary = "Gera uma análise de tendências de estoque", description = "Retorna uma análise simplificada das tendências de estoque dos produtos.")
    @GetMapping("/analise-tendencias-estoque")
    public ResponseEntity<List<Map<String, Object>>> gerarAnaliseTendenciasEstoque() {
        List<Map<String, Object>> analise = relatorioService.gerarAnaliseTendenciasEstoque();
        return ResponseEntity.ok(analise);
    }
}