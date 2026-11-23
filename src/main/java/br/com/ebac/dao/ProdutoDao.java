package br.com.ebac.dao;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import br.com.ebac.domain.Produto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class ProdutoDao implements IProdutoDao {

    private final EntityManagerFactory emf;

    public ProdutoDao() {
        this(JPAUtil.getEntityManagerFactory());
    }

    public ProdutoDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Produto cadastrar(Produto produto) {
        return emTransacao(em -> {
            em.persist(produto);
            return produto;
        });
    }

    @Override
    public Produto buscarPorId(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Produto.class, id);
        }
    }

    @Override
    public Optional<Produto> buscarPorNome(String nome) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT p FROM Produto p WHERE p.nome = :nome", Produto.class)
                    .setParameter("nome", nome)
                    .getResultStream()
                    .findFirst();
        }
    }

    @Override
    public List<Produto> buscarTodos() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT p FROM Produto p ORDER BY p.id", Produto.class).getResultList();
        }
    }

    @Override
    public Produto alterar(Produto produto) {
        return emTransacao(em -> em.merge(produto));
    }

    @Override
    public void excluir(Produto produto) {
        emTransacao(em -> {
            Produto gerenciado = em.find(Produto.class, produto.getId());
            if (gerenciado != null) {
                em.remove(gerenciado);
            }
            return null;
        });
    }

    private <T> T emTransacao(Function<EntityManager, T> acao) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                T resultado = acao.apply(em);
                em.getTransaction().commit();
                return resultado;
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
    }
}
