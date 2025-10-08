package estoque.com.project.Services;

import estoque.com.project.DTO.CategoriaRequestDTO;
import estoque.com.project.DTO.CategoriaResponseDTO;
import estoque.com.project.Exceptions.BusinessException;
import estoque.com.project.Exceptions.ResourceNotFoundException;
import estoque.com.project.Models.Categoria;
import estoque.com.project.Repositories.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> findAll() {
        return categoriaRepository.findAll().stream()
                .map(categoria -> modelMapper.map(categoria, CategoriaResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
        return modelMapper.map(categoria, CategoriaResponseDTO.class);
    }

    @Transactional
    public CategoriaResponseDTO create(CategoriaRequestDTO categoriaDTO) {
        if (categoriaRepository.findByNome(categoriaDTO.getNome()).isPresent()) {
            throw new BusinessException("Já existe uma categoria com o nome: " + categoriaDTO.getNome());
        }
        Categoria categoria = modelMapper.map(categoriaDTO, Categoria.class);
        categoria = categoriaRepository.save(categoria);
        return modelMapper.map(categoria, CategoriaResponseDTO.class);
    }

    @Transactional
    public CategoriaResponseDTO update(Long id, CategoriaRequestDTO categoriaDTO) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));

        categoriaRepository.findByNome(categoriaDTO.getNome()).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new BusinessException("Já existe outra categoria com o nome: " + categoriaDTO.getNome());
            }
        });

        modelMapper.map(categoriaDTO, categoriaExistente);
        categoriaExistente.setId(id); // Garante que o ID não seja alterado pelo mapeamento
        categoriaExistente = categoriaRepository.save(categoriaExistente);
        return modelMapper.map(categoriaExistente, CategoriaResponseDTO.class);
    }

    @Transactional
    public void delete(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada com ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    // Método auxiliar para obter a entidade Categoria
    @Transactional(readOnly = true)
    public Categoria getCategoriaEntity(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
    }
}