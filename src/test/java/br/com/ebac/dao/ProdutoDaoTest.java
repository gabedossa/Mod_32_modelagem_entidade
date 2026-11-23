package br.com.ebac.dao;

import br.com.ebac.domain.Produto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes do CRUD. Cada teste apaga apenas os produtos que ele mesmo criou,
 * então os dados já existentes no banco (ex.: os eletrônicos) são preservados.
 */
class ProdutoDaoTest {

    private IProdutoDao dao;
    private final List<Produto> criados = new ArrayList<>();

    @BeforeEach
    void setUp() {
        dao = new ProdutoDao();
    }

    @AfterEach
    void limpar() {
        criados.forEach(dao::excluir);
        criados.clear();
    }

    private Produto cadastrar(Produto produto) {
        Produto salvo = dao.cadastrar(produto);
        criados.add(salvo);
        return salvo;
    }

    @Test
    void deveCadastrarProdutoComIdGerado() {
        Produto produto = cadastrar(new Produto("Caneta", "Caneta esferográfica azul", 2.50, 100));

        assertNotNull(produto.getId());
        assertTrue(produto.getId() > 0);
    }

    @Test
    void deveBuscarProdutoPorId() {
        Produto produto = cadastrar(new Produto("Caderno", "Caderno 10 matérias", 25.90, 30));

        Produto encontrado = dao.buscarPorId(produto.getId());

        assertNotNull(encontrado);
        assertEquals("Caderno", encontrado.getNome());
        assertEquals("Caderno 10 matérias", encontrado.getDescricao());
        assertEquals(25.90, encontrado.getPreco(), 0.001);
        assertEquals(30, encontrado.getEstoque());
    }

    @Test
    void deveAlterarProduto() {
        Produto produto = cadastrar(new Produto("Lápis", "Lápis preto", 1.00, 50));

        produto.setNome("Lápis HB");
        produto.setPreco(1.50);
        produto.setEstoque(45);
        dao.alterar(produto);

        Produto alterado = dao.buscarPorId(produto.getId());
        assertEquals("Lápis HB", alterado.getNome());
        assertEquals(1.50, alterado.getPreco(), 0.001);
        assertEquals(45, alterado.getEstoque());
    }

    @Test
    void deveExcluirProduto() {
        Produto produto = dao.cadastrar(new Produto("Borracha", "Borracha branca", 0.75, 200));

        dao.excluir(produto);

        assertNull(dao.buscarPorId(produto.getId()));
    }

    @Test
    void deveBuscarTodosOsProdutos() {
        int antes = dao.buscarTodos().size();
        cadastrar(new Produto("Régua", "Régua 30cm", 3.20, 40));
        cadastrar(new Produto("Mochila", "Mochila escolar", 89.90, 10));

        assertEquals(antes + 2, dao.buscarTodos().size());
    }
}
