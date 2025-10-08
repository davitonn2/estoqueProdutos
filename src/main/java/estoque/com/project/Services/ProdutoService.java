package estoque.com.project.Services;

import estoque.com.project.DTO.ProdutoRequestDTO;
import estoque.com.project.DTO.ProdutoResponseDTO;
import estoque.com.project.Exceptions.BusinessException;
import estoque.com.project.Exceptions.ResourceNotFoundException;
import estoque.com.project.Models.Categoria;
import estoque.com.project.Models.Fornecedor;
import estoque.com.project.Models.Produto;
import estoque.com.project.Repositories.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaService categoriaService; // Injetar CategoriaService para buscar Categoria
    private final FornecedorService fornecedorService; // Injetar FornecedorService para buscar Fornecedor
    private final NotificacaoService notificacaoService;
    private final ModelMapper modelMapper;

    private static final int LIMITE_ESTOQUE_BAIXO = 5; // Limite configurável para alerta de estoque

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> findAll() {
        return produtoRepository.findAll().stream()
                .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO findById(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
        return modelMapper.map(produto, ProdutoResponseDTO.class);
    }

    @Transactional
    public ProdutoResponseDTO create(ProdutoRequestDTO produtoDTO) {
        Categoria categoria = categoriaService.getCategoriaEntity(produtoDTO.getCategoriaId());
        Fornecedor fornecedor = fornecedorService.getFornecedorEntity(produtoDTO.getFornecedorId());

        Produto produto = modelMapper.map(produtoDTO, Produto.class);
        produto.setCategoria(categoria);
        produto.setFornecedor(fornecedor);

        produto = produtoRepository.save(produto);

        // Verifica estoque após criação
        verificarEstoqueEAlertar(produto);

        return modelMapper.map(produto, ProdutoResponseDTO.class);
    }

    @Transactional
    public ProdutoResponseDTO update(Long id, ProdutoRequestDTO produtoDTO) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        Categoria categoria = categoriaService.getCategoriaEntity(produtoDTO.getCategoriaId());
        Fornecedor fornecedor = fornecedorService.getFornecedorEntity(produtoDTO.getFornecedorId());

        // Mapeia DTO para a entidade existente, atualizando os campos
        modelMapper.map(produtoDTO, produtoExistente);
        produtoExistente.setId(id); // Garante que o ID não seja alterado
        produtoExistente.setCategoria(categoria);
        produtoExistente.setFornecedor(fornecedor);

        produtoExistente = produtoRepository.save(produtoExistente);

        // Verifica estoque após atualização
        verificarEstoqueEAlertar(produtoExistente);

        return modelMapper.map(produtoExistente, ProdutoResponseDTO.class);
    }

    @Transactional
    public void delete(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + id);
        }
        // TODO: Adicionar lógica para verificar se há itens de pedido associados antes de deletar
        produtoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> findByCategoriaNome(String nomeCategoria) {
        return produtoRepository.findByCategoriaNome(nomeCategoria).stream()
                .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> findByNomeContaining(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> findProdutosComBaixoEstoque() {
        return produtoRepository.findProdutosComBaixoEstoque(LIMITE_ESTOQUE_BAIXO).stream()
                .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void diminuirEstoque(Long produtoId, Integer quantidade) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + produtoId));

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new BusinessException(
                    "Estoque insuficiente para o produto '" + produto.getNome() + "'. Disponível: " + produto.getQuantidadeEstoque() + ", Solicitado: " + quantidade);
        }
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepository.save(produto);
        verificarEstoqueEAlertar(produto); // Verifica após a diminuição
    }

    @Transactional
    public void aumentarEstoque(Long produtoId, Integer quantidade) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + produtoId));

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
        produtoRepository.save(produto);
        // Não é necessário alertar para aumento de estoque, mas pode ser útil para logs
        log.info("Estoque do produto {} (ID: {}) aumentado em {} unidades. Novo estoque: {}",
                produto.getNome(), produto.getId(), quantidade, produto.getQuantidadeEstoque());
    }

    // Método privado para verificar estoque e alertar
    private void verificarEstoqueEAlertar(Produto produto) {
        if (produto.getQuantidadeEstoque() < LIMITE_ESTOQUE_BAIXO) {
            notificacaoService.enviarAlertaEstoqueBaixo(produto);
        }
    }

    // Agendador para verificar estoque periodicamente (ex: a cada 60 minutos)
    @Scheduled(fixedRate = 3600000) // 3600000 ms = 1 hora
    public void verificarEstoquePeriodicamente() {
        log.info("Executando verificação de estoque agendada...");
        List<Produto> produtosBaixoEstoque = produtoRepository.findProdutosComBaixoEstoque(LIMITE_ESTOQUE_BAIXO);
        produtosBaixoEstoque.forEach(produto -> {
            log.warn("Produto com estoque baixo detectado pelo agendador: {} (ID: {}), Estoque: {}",
                    produto.getNome(), produto.getId(), produto.getQuantidadeEstoque());
            notificacaoService.enviarAlertaEstoqueBaixo(produto);
        });
        log.info("Verificação de estoque agendada concluída. {} produtos com baixo estoque encontrados.", produtosBaixoEstoque.size());
    }

    // Método auxiliar para obter a entidade Produto
    @Transactional(readOnly = true)
    public Produto getProdutoEntity(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
    }
}