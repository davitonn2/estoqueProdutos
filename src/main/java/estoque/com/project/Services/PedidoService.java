package estoque.com.project.Services;

import estoque.com.project.DTO.ItemPedidoRequestDTO;
import estoque.com.project.DTO.ItemPedidoResponseDTO;
import estoque.com.project.DTO.PedidoRequestDTO;
import estoque.com.project.DTO.PedidoResponseDTO;
import estoque.com.project.Exceptions.BusinessException;
import estoque.com.project.Exceptions.ResourceNotFoundException;
import estoque.com.project.Models.ItemPedido;
import estoque.com.project.Models.Pedido;
import estoque.com.project.Models.Produto;
import estoque.com.project.Models.StatusPedido;
import estoque.com.project.Repositories.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoService produtoService; // Usar ProdutoService para manipular estoque
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findAll() {
        return pedidoRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO findById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));
        return convertToResponseDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO create(PedidoRequestDTO pedidoDTO) {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setValorTotal(BigDecimal.ZERO); // Será recalculado pelo @PrePersist/@PreUpdate

        // Processar itens do pedido
        for (ItemPedidoRequestDTO itemDTO : pedidoDTO.getItens()) {
            Produto produto = produtoService.getProdutoEntity(itemDTO.getProdutoId()); // Obtém entidade Produto

            // Verifica estoque antes de adicionar ao pedido
            if (produto.getQuantidadeEstoque() < itemDTO.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente para o produto '" + produto.getNome() + "' (ID: " + produto.getId() + "). " +
                        "Disponível: " + produto.getQuantidadeEstoque() + ", Solicitado: " + itemDTO.getQuantidade());
            }

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setPrecoUnitario(produto.getPreco()); // Usa o preço atual do produto

            pedido.adicionarItem(itemPedido);

            // Diminui o estoque do produto imediatamente
            produtoService.diminuirEstoque(produto.getId(), itemDTO.getQuantidade());
        }

        pedido = pedidoRepository.save(pedido);
        return convertToResponseDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO updateStatus(Long id, StatusPedido novoStatus) {
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));

        // Lógica de transição de status (ex: não permitir ir de CONCLUIDO para PENDENTE)
        if (pedidoExistente.getStatus().equals(StatusPedido.CONCLUIDO) && novoStatus.equals(StatusPedido.PENDENTE)) {
            throw new BusinessException("Não é possível reverter um pedido concluído para pendente.");
        }
        if (pedidoExistente.getStatus().equals(StatusPedido.CANCELADO)) {
            throw new BusinessException("Não é possível alterar o status de um pedido cancelado.");
        }

        // Se o status mudar para CANCELADO, o estoque deve ser restaurado
        if (novoStatus.equals(StatusPedido.CANCELADO) && !pedidoExistente.getStatus().equals(StatusPedido.CANCELADO)) {
            for (ItemPedido item : pedidoExistente.getItens()) {
                produtoService.aumentarEstoque(item.getProduto().getId(), item.getQuantidade());
            }
        }
        // Se o status mudar de CANCELADO para outro, o estoque deve ser diminuído novamente (se houver)
        // Isso é mais complexo e pode requerer verificação de estoque antes de permitir a mudança
        // Para simplificar, focaremos apenas no cancelamento restaurando o estoque.

        pedidoExistente.setStatus(novoStatus);
        pedidoExistente = pedidoRepository.save(pedidoExistente);
        return convertToResponseDTO(pedidoExistente);
    }


    @Transactional
    public void delete(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));

        // Antes de deletar, se o pedido não estiver concluído, restaura o estoque
        if (!pedido.getStatus().equals(StatusPedido.CONCLUIDO)) {
            for (ItemPedido item : pedido.getItens()) {
                produtoService.aumentarEstoque(item.getProduto().getId(), item.getQuantidade());
            }
        }
        pedidoRepository.delete(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findPedidosBetweenDates(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return pedidoRepository.findPedidosBetweenDates(dataInicio, dataFim).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private PedidoResponseDTO convertToResponseDTO(Pedido pedido) {
        PedidoResponseDTO responseDTO = modelMapper.map(pedido, PedidoResponseDTO.class);
        responseDTO.setItens(pedido.getItens().stream()
                .map(item -> {
                    ItemPedidoResponseDTO itemResponse = modelMapper.map(item, ItemPedidoResponseDTO.class);
                    itemResponse.setProdutoId(item.getProduto().getId());
                    itemResponse.setProdutoNome(item.getProduto().getNome());
                    itemResponse.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
                    return itemResponse;
                })
                .collect(Collectors.toList()));
        return responseDTO;
    }

    // Método auxiliar para obter a entidade Pedido
    @Transactional(readOnly = true)
    public Pedido getPedidoEntity(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));
    }
}
