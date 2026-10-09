package com.fatec.itu.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatec.itu.demo.dtos.ClienteRequestDTO;
import com.fatec.itu.demo.dtos.ClienteResponseDTO;
import com.fatec.itu.demo.entites.Cliente;
import com.fatec.itu.demo.repositories.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public List<ClienteResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ClienteResponseDTO findById(Long id) {
        return toResponseDTO(buscarCliente(id));
    }

    public ClienteResponseDTO save(ClienteRequestDTO dto) {
        validarCliente(dto);

        if (repository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este CPF.");
        }

        Cliente cliente = new Cliente();
        preencherCliente(cliente, dto);
        return toResponseDTO(repository.save(cliente));
    }

    public ClienteResponseDTO update(ClienteRequestDTO dto, Long id) {
        validarCliente(dto);

        Cliente clienteAtual = buscarCliente(id);

        if (!dto.getCpf().equals(clienteAtual.getCpf()) && repository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este CPF.");
        }

        preencherCliente(clienteAtual, dto);
        return toResponseDTO(repository.save(clienteAtual));
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Cliente não cadastrado.");
        }
        repository.deleteById(id);
    }

    private Cliente buscarCliente(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não cadastrado."));
    }

    private void validarCliente(ClienteRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Os dados do cliente são obrigatórios.");
        }
        if (isBlank(dto.getNome())) {
            throw new IllegalArgumentException("O nome do cliente é obrigatório.");
        }
        if (isBlank(dto.getEmail())) {
            throw new IllegalArgumentException("O e-mail do cliente é obrigatório.");
        }
        if (isBlank(dto.getCpf())) {
            throw new IllegalArgumentException("O CPF do cliente é obrigatório.");
        }

        dto.setNome(dto.getNome().trim());
        dto.setEmail(dto.getEmail().trim());
        dto.setCpf(dto.getCpf().trim());
        if (dto.getTelefone() != null) {
            dto.setTelefone(dto.getTelefone().trim());
        }
    }

    private void preencherCliente(Cliente cliente, ClienteRequestDTO dto) {
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setCpf(dto.getCpf());
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getCpf());
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
