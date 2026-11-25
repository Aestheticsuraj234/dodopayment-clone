package dev.payforge.identity.merchant;

import java.time.Instant;

public record Merchant(String id , String name , String email , Instant createdAt) {
    
}
