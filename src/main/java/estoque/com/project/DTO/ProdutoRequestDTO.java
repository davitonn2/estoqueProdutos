package estoque.com.project.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoRequestDTO {

    @NotBlank(message = "O nome do produto não pode ser vazio")
    @Size(min = 2, max = 150, message = "O nome do produto deve ter entre 2 e 150 caracteres")
    private String nome;

    @Size(max = 500, message = "A descrição do produto não pode exceder 500 caracteres")
    private String descricao;

    @NotNull(message = "O preço não pode ser nulo")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message = "A quantidade em estoque não pode ser nula")
    @Min(value = 0, message = "A quantidade em estoque não pode ser negativa")
    private Integer quantidadeEstoque;

    @NotNull(message = "O ID da categoria não pode ser nulo")
    private Long categoriaId;

    @NotNull(message = "O ID do fornecedor não pode ser nulo")
    private Long fornecedorId;
}
