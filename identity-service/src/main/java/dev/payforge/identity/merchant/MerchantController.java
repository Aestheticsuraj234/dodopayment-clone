package dev.payforge.identity.merchant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/v1/merchants")
public class MerchantController {

   record CreateMerchantRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 254) String email) {}

    private final MerchantRepository merchants;

    public MerchantController(MerchantRepository merchants) {
        this.merchants = merchants;
    }

    @PostMapping
   ResponseEntity<Merchant> create(@Valid @RequestBody CreateMerchantRequest request)  {
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