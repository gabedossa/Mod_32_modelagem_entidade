package br.com.ebac.dao;

import br.com.ebac.domain.Produto;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoDaoTest {

    private static EntityManagerFactory emf;
    private IProdutoDao dao;

    @BeforeAll
    static void iniciarFabrica() {
        emf = Persistence.createEntityManagerFactory("ExemploJPA");
    }

    @AfterAll
    static void fecharFabrica() {
        emf.close();
    }

    @BeforeEach
    void setUp() {
        dao = new ProdutoDao(emf);
    }

    @AfterEach
    void limpar() {
        dao.buscarTodos().forEach(dao::excluir);
    }

    @Test
    void deveCadastrarEBuscarProduto() {
        Produto produto = dao.cadastrar(new Produto("A1", "Caneta", new BigDecimal("2.50")));

        assertNotNull(produto.getId());
        Produto encontrado = dao.buscarPorId(produto.getId());
        assertNotNull(encontrado);
        assertEquals("Caneta", encontrado.getNome());
    }

    @Test
    void deveAlterarProduto() {
        Produto produto = dao.cadastrar(new Produto("A2", "Lápis", new BigDecimal("1.00")));

        produto.setNome("Lápis HB");
        dao.alterar(produto);

        assertEquals("Lápis HB", dao.buscarPorId(produto.getId()).getNome());
    }

    @Test
    void deveExcluirProduto() {
        Produto produto = dao.cadastrar(new Produto("A3", "Borracha", new BigDecimal("0.75")));

        dao.excluir(produto);

        assertNull(dao.buscarPorId(produto.getId()));
    }

    @Test
    void deveBuscarTodos() {
        dao.cadastrar(new Produto("B1", "Caderno", new BigDecimal("15.90")));
        dao.cadastrar(new Produto("B2", "Régua", new BigDecimal("3.20")));

        assertEquals(2, dao.buscarTodos().size());
    }
}
