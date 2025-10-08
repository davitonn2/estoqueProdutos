package estoque.com.project.Repositories;

import estoque.com.project.Models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByCategoriaNome(String nomeCategoria);
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    List<Produto> findByQuantidadeEstoqueLessThan(Integer quantidade);

    @Query("SELECT p FROM Produto p WHERE p.quantidadeEstoque < :limiteEstoque")
    List<Produto> findProdutosComBaixoEstoque(@Param("limiteEstoque") Integer limiteEstoque);

    // Consulta para produtos mais vendidos
    @Query("SELECT ip.produto, SUM(ip.quantidade) as totalVendido " +
            "FROM ItemPedido ip GROUP BY ip.produto ORDER BY totalVendido DESC")
    List<Object[]> findProdutosMaisVendidos();
}
