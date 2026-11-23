package br.com.ebac.dao;

import br.com.ebac.domain.Produto;

import java.util.List;

public interface IProdutoDao {

    Produto cadastrar(Produto produto);

    Produto buscarPorId(Long id);

    List<Produto> buscarTodos();

    Produto alterar(Produto produto);

    void excluir(Produto produto);
}
