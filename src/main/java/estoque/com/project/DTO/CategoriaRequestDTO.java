package estoque.com.project.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequestDTO {

    @NotBlank(message = "O nome da categoria não pode ser vazio")
    @Size(min = 2, max = 100, message = "O nome da categoria deve ter entre 2 e 100 caracteres")
    private String nome;

    @Size(max = 255, message = "A descrição da categoria não pode exceder 255 caracteres")
    private String descricao;
}
