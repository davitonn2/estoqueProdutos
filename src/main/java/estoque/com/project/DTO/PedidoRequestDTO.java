package estoque.com.project.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {

    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    @Valid
    private List<ItemPedidoRequestDTO> itens;
}