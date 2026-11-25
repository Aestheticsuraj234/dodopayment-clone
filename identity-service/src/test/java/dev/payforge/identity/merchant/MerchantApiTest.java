package dev.payforge.identity.merchant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.jayway.jsonpath.JsonPath;

import dev.payforge.identity.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class MerchantApiTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void createsAMerchantAndReadsItBack() throws Exception {
        MvcTestResult created = createMerchant("Acme Software", "create@acme.test");
        assertThat(created).hasStatus(HttpStatus.CREATED);

        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");
        assertThat(id).startsWith("mer_");

        assertThat(mvc.get().uri("/v1/merchants/{id}", id))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.email").isEqualTo("create@acme.test");
    }

    @Test
    void rejectsADuplicateEmailWith409() {
        assertThat(createMerchant("First", "dup@acme.test")).hasStatus(HttpStatus.CREATED);

        assertThat(createMerchant("Second", "dup@acme.test"))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.code").isEqualTo("email_taken");
    }

    @Test
    void rejectsInvalidInputWith400() {
        MvcTestResult result = createMerchant("", "not-an-email");

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.errors.name").isEqualTo("must not be blank");
        assertThat(result).bodyJson().extractingPath("$.errors.email").isEqualTo("must be a well-formed email address");
    }

    @Test
    void returns404ForAnUnknownMerchant() {
        assertThat(mvc.get().uri("/v1/merchants/mer_does_not_exist"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    private MvcTestResult createMerchant(String name, String email) {
        return mvc.post().uri("/v1/merchants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "%s", "email": "%s"}
                        """.formatted(name, email))
                .exchange();
    }
}