package dev.payforge.identity.merchant;

public class MerchantEmailTakenException extends RuntimeException {

    public MerchantEmailTakenException(String email) {
        super("A merchant with email " + email + " already exists");
    }
}