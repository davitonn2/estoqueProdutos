package estoque.com.project.Services;

import estoque.com.project.DTO.FornecedorRequestDTO;
import estoque.com.project.DTO.FornecedorResponseDTO;
import estoque.com.project.Exceptions.BusinessException;
import estoque.com.project.Exceptions.ResourceNotFoundException;
import estoque.com.project.Models.Fornecedor;
import estoque.com.project.Repositories.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<FornecedorResponseDTO> findAll() {
        return fornecedorRepository.findAll().stream()
                .map(fornecedor -> modelMapper.map(fornecedor, FornecedorResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FornecedorResponseDTO findById(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + id));
        return modelMapper.map(fornecedor, FornecedorResponseDTO.class);
    }

    @Transactional
    public FornecedorResponseDTO create(FornecedorRequestDTO fornecedorDTO) {
        // Validações de unicidade para CNPJ e Email
        fornecedorRepository.findByCnpj(fornecedorDTO.getCnpj()).ifPresent(f -> {
            throw new BusinessException("Já existe um fornecedor com o CNPJ: " + fornecedorDTO.getCnpj());
        });
        fornecedorRepository.findByEmail(fornecedorDTO.getEmail()).ifPresent(f -> {
            throw new BusinessException("Já existe um fornecedor com o e-mail: " + fornecedorDTO.getEmail());
        });

        Fornecedor fornecedor = modelMapper.map(fornecedorDTO, Fornecedor.class);
        fornecedor = fornecedorRepository.save(fornecedor);
        return modelMapper.map(fornecedor, FornecedorResponseDTO.class);
    }

    @Transactional
    public FornecedorResponseDTO update(Long id, FornecedorRequestDTO fornecedorDTO) {
        Fornecedor fornecedorExistente = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + id));

        // Validações de unicidade para CNPJ e Email (ignorando o próprio fornecedor)
        fornecedorRepository.findByCnpj(fornecedorDTO.getCnpj()).ifPresent(f -> {
            if (!f.getId().equals(id)) {
                throw new BusinessException("Já existe outro fornecedor com o CNPJ: " + fornecedorDTO.getCnpj());
            }
        });
        fornecedorRepository.findByEmail(fornecedorDTO.getEmail()).ifPresent(f -> {
            if (!f.getId().equals(id)) {
                throw new BusinessException("Já existe outro fornecedor com o e-mail: " + fornecedorDTO.getEmail());
            }
        });

        modelMapper.map(fornecedorDTO, fornecedorExistente);
        fornecedorExistente.setId(id); // Garante que o ID não seja alterado pelo mapeamento
        fornecedorExistente = fornecedorRepository.save(fornecedorExistente);
        return modelMapper.map(fornecedorExistente, FornecedorResponseDTO.class);
    }

    @Transactional
    public void delete(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fornecedor não encontrado com ID: " + id);
        }
        // TODO: Adicionar lógica para verificar se há produtos ou pedidos associados antes de deletar
        fornecedorRepository.deleteById(id);
    }

    // Método auxiliar para obter a entidade Fornecedor
    @Transactional(readOnly = true)
    public Fornecedor getFornecedorEntity(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + id));
    }
}