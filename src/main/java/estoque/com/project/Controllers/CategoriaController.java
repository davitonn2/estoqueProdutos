package estoque.com.project.Controllers;

import estoque.com.project.DTO.CategoriaRequestDTO;
import estoque.com.project.DTO.CategoriaResponseDTO;
import estoque.com.project.Services.CategoriaService;
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
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "API para gerenciamento de categorias de produtos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Operation(summary = "Lista todas as categorias", description = "Retorna uma lista de todas as categorias cadastradas")
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> findAll() {
        List<CategoriaResponseDTO> categorias = categoriaService.findAll();
        return ResponseEntity.ok(categorias);
    }

    @Operation(summary = "Busca uma categoria por ID", description = "Retorna uma categoria específica pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> findById(@PathVariable Long id) {
        CategoriaResponseDTO categoria = categoriaService.findById(id);
        return ResponseEntity.ok(categoria);
    }

    @Operation(summary = "Cadastra uma nova categoria", description = "Cria uma nova categoria no sistema")
    @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou categoria já existente")
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> create(@Valid @RequestBody CategoriaRequestDTO categoriaDTO) {
        CategoriaResponseDTO novaCategoria = categoriaService.create(categoriaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
    }

    @Operation(summary = "Atualiza uma categoria existente", description = "Atualiza os dados de uma categoria pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome de categoria já existente")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO categoriaDTO) {
        CategoriaResponseDTO categoriaAtualizada = categoriaService.update(id, categoriaDTO);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @Operation(summary = "Exclui uma categoria", description = "Remove uma categoria do sistema pelo seu ID")
    @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}