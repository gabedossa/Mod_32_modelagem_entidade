package br.com.ebac.dao;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Cria a EntityManagerFactory da unidade "ExemploJPA".
 * URL, usuário e senha do banco podem ser sobrescritos pelas variáveis de ambiente
 * DB_URL, DB_USER e DB_PASSWORD, para não deixar a senha real no persistence.xml.
 */
public final class JPAUtil {

    private static EntityManagerFactory emf;

    private JPAUtil() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            Map<String, String> props = new HashMap<>();
            sobrescrever(props, "DB_URL", "jakarta.persistence.jdbc.url");
            sobrescrever(props, "DB_USER", "jakarta.persistence.jdbc.user");
            sobrescrever(props, "DB_PASSWORD", "jakarta.persistence.jdbc.password");
            emf = Persistence.createEntityManagerFactory("ExemploJPA", props);
        }
        return emf;
    }

    private static void sobrescrever(Map<String, String> props, String variavel, String propriedade) {
        String valor = System.getenv(variavel);
        if (valor != null && !valor.isBlank()) {
            props.put(propriedade, valor);
        }
    }
}
