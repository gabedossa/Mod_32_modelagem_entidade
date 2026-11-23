package br.com.ebac.dao;

import br.com.ebac.domain.Produto;

import java.util.List;
import java.util.Optional;

public interface IProdutoDao {

    Produto cadastrar(Produto produto);

    Produto buscarPorId(Long id);

    Optional<Produto> buscarPorNome(String nome);

    List<Produto> buscarTodos();

    Produto alterar(Produto produto);

    void excluir(Produto produto);
}
