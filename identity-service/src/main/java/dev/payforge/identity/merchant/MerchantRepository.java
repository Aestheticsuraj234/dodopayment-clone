package dev.payforge.identity.merchant;

import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class MerchantRepository {

    private final JdbcClient jdbc;

    public MerchantRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

 public Merchant create(String name, String email) {
    String id = "mer_" + UUID.randomUUID().toString().replace("-", "");
    try {
        return jdbc.sql("""
                INSERT INTO merchants (id, name, email)
                VALUES (:id, :name, :email)
                RETURNING id, name, email, created_at
                """)
                .param("id", id)
                .param("name", name)
                .param("email", email)
                .query(Merchant.class)
                .single();
    } catch (DuplicateKeyException e) {
        throw new MerchantEmailTakenException(email);
    }
}

    public Optional<Merchant> findById(String id) {
        return jdbc.sql("""
                SELECT id, name, email, created_at
                FROM merchants
                WHERE id = :id
                """)
                .param("id", id)
                .query(Merchant.class)
                .optional();
    }
}