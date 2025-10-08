package estoque.com.project.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorRequestDTO {

    @NotBlank(message = "O nome do fornecedor não pode ser vazio")
    @Size(min = 2, max = 150, message = "O nome do fornecedor deve ter entre 2 e 150 caracteres")
    private String nome;

    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter exatamente 14 dígitos")
    private String cnpj;

    @NotBlank(message = "O e-mail do fornecedor não pode ser vazio")
    @Email(message = "Formato de e-mail inválido")
    @Size(max = 100, message = "O e-mail não pode exceder 100 caracteres")
    private String email;

    @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter 10 ou 11 dígitos")
    private String telefone;

    @Size(max = 255, message = "O endereço não pode exceder 255 caracteres")
    private String endereco;
}