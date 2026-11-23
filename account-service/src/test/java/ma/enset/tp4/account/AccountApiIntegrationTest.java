package ma.enset.tp4.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test void createCreditDebitAndGetHistory() throws Exception {
        String body = mvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"initialBalance\":1000,\"currency\":\"MAD\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId", not(isEmptyOrNullString())))
                .andReturn().getResponse().getContentAsString();
        String id = json.readTree(body).get("accountId").asText();
        mvc.perform(post("/api/accounts/{id}/credit", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":250}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/accounts/{id}/debit", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":125}"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/accounts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(1125.00)));
        mvc.perform(get("/api/accounts/{id}/events", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test void invalidCurrencyRejected() throws Exception {
        mvc.perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"initialBalance\":100,\"currency\":\"INVALID\"}"))
                .andExpect(status().isBadRequest());
    }
}
