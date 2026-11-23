package br.com.ebac.dao;

import br.com.ebac.domain.Produto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cadastra 5 produtos eletrônicos e os mantém no banco (não são apagados).
 * Se um produto com o mesmo nome já existir, ele não é cadastrado de novo.
 */
class CargaEletronicosTest {

    private final IProdutoDao dao = new ProdutoDao();

    private static final List<Produto> ELETRONICOS = List.of(
            new Produto("Smartphone Galaxy S24", "Smartphone 256GB, tela 6.2\", 8GB RAM", 4299.00, 15),
            new Produto("Notebook Dell Inspiron 15", "Intel Core i7, 16GB RAM, SSD 512GB", 5499.90, 8),
            new Produto("Smart TV LG 55\" 4K", "Smart TV LED 55 polegadas, UHD 4K, webOS", 2899.00, 12),
            new Produto("Fone JBL Tune 520BT", "Fone de ouvido Bluetooth, até 57h de bateria", 249.90, 40),
            new Produto("Console PlayStation 5", "Console PS5 Slim, 1TB, com controle DualSense", 3799.00, 6)
    );

    @Test
    void deveCadastrarCincoEletronicos() {
        for (Produto eletronico : ELETRONICOS) {
            if (dao.buscarPorNome(eletronico.getNome()).isEmpty()) {
                dao.cadastrar(eletronico);
            }
        }

        for (Produto eletronico : ELETRONICOS) {
            Produto salvo = dao.buscarPorNome(eletronico.getNome()).orElse(null);
            assertNotNull(salvo, "Não encontrado no banco: " + eletronico.getNome());
            assertNotNull(salvo.getId());
            assertEquals(eletronico.getPreco(), salvo.getPreco(), 0.001);
            assertEquals(eletronico.getEstoque(), salvo.getEstoque());
        }
    }
}
