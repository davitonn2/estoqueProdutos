package estoque.com.project.DTO;

import estoque.com.project.Models.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    private Long id;
    private LocalDateTime dataPedido;
    private List<ItemPedidoResponseDTO> itens;
    private BigDecimal valorTotal;
    private StatusPedido status;
}
