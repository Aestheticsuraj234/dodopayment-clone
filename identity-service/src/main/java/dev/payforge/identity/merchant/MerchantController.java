package dev.payforge.identity.merchant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/merchants")
public class MerchantController {

    record CreateMerchantRequest(String name, String email) {}

    private final MerchantRepository merchants;

    public MerchantController(MerchantRepository merchants) {
        this.merchants = merchants;
    }

    @PostMapping
    ResponseEntity<Merchant> create(@RequestBody CreateMerchantRequest request) {
        Merchant merchant = merchants.create(request.name(), request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(merchant);
    }

    @GetMapping("/{id}")
    ResponseEntity<Merchant> get(@PathVariable String id) {
        return merchants.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}