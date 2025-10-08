package estoque.com.project.Controllers;

import estoque.com.project.DTO.FornecedorRequestDTO;
import estoque.com.project.DTO.FornecedorResponseDTO;
import estoque.com.project.Services.FornecedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
@RequiredArgsConstructor
@Tag(name = "Fornecedores", description = "API para gerenciamento de fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    @Operation(summary = "Lista todos os fornecedores", description = "Retorna uma lista de todos os fornecedores cadastrados")
    @GetMapping
    public ResponseEntity<List<FornecedorResponseDTO>> findAll() {
        List<FornecedorResponseDTO> fornecedores = fornecedorService.findAll();
        return ResponseEntity.ok(fornecedores);
    }

    @Operation(summary = "Busca um fornecedor por ID", description = "Retorna um fornecedor específico pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Fornecedor encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> findById(@PathVariable Long id) {
        FornecedorResponseDTO fornecedor = fornecedorService.findById(id);
        return ResponseEntity.ok(fornecedor);
    }

    @Operation(summary = "Cadastra um novo fornecedor", description = "Cria um novo fornecedor no sistema")
    @ApiResponse(responseCode = "201", description = "Fornecedor criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou fornecedor já existente (CNPJ/Email)")
    @PostMapping
    public ResponseEntity<FornecedorResponseDTO> create(@Valid @RequestBody FornecedorRequestDTO fornecedorDTO) {
        FornecedorResponseDTO novoFornecedor = fornecedorService.create(fornecedorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoFornecedor);
    }

    @Operation(summary = "Atualiza um fornecedor existente", description = "Atualiza os dados de um fornecedor pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Fornecedor atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou CNPJ/Email já existente para outro fornecedor")
    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> update(@PathVariable Long id, @Valid @RequestBody FornecedorRequestDTO fornecedorDTO) {
        FornecedorResponseDTO fornecedorAtualizado = fornecedorService.update(id, fornecedorDTO);
        return ResponseEntity.ok(fornecedorAtualizado);
    }

    @Operation(summary = "Exclui um fornecedor", description = "Remove um fornecedor do sistema pelo seu ID")
    @ApiResponse(responseCode = "204", description = "Fornecedor excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fornecedorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}