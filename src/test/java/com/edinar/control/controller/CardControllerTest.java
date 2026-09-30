package com.edinar.control.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CardControllerTest {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
        .withDatabaseName("edinar_control_test")
        .withUsername("edinar_test")
        .withPassword("edinar_test");

    static {
        postgres.start();
    }

    @AfterAll
    static void stopPostgres() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.clean-disabled", () -> false);
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    }

    @Test
    void testCardLifecycleAndLedgerIntegration() throws Exception {
        String authPayload = """
            {
              "email": "card-test@example.com",
              "password": "StrongPassword123!",
              "displayName": "Card Test"
            }
            """;

        MvcResult authResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authPayload))
            .andReturn();
        if (authResult.getResponse().getStatus() != 201) {
            throw new AssertionError("Registration failed: " + authResult.getResponse().getContentAsString());
        }
        String token = authResult.getResponse().getContentAsString()
            .replaceFirst(".*\\\"accessToken\\\"\\s*:\\s*\\\"([^\\\"]+)\\\".*", "$1");

        // 1. POST /api/cards
        String createPayload = """
            {
              "name": "Carte 01",
              "maskedNumber": "**** **** **** 4821",
              "provider": "LA_POSTE",
              "currency": "TND",
              "initialBalance": 654.710,
              "notes": "Carte principale"
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/cards")
                .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
                .content(createPayload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.name", equalTo("Carte 01")))
            .andExpect(jsonPath("$.currentBalance", equalTo(654.710)))
            .andExpect(jsonPath("$.status", equalTo("ACTIVE")))
            .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        // Extract ID using simple string extraction or regex
        // e.g. "id": 1
        int idStart = responseBody.indexOf("\"id\":") + 5;
        int idEnd = responseBody.indexOf(",", idStart);
        String idStr = responseBody.substring(idStart, idEnd).trim();
        long cardId = Long.parseLong(idStr);

        // 2. GET /api/cards
        mockMvc.perform(get("/api/cards").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        // 3. GET /api/cards/{id}
        mockMvc.perform(get("/api/cards/" + cardId).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", equalTo((int) cardId)))
            .andExpect(jsonPath("$.name", equalTo("Carte 01")));

        // 4. PUT /api/cards/{id}
        String updatePayload = """
            {
              "name": "Carte 01 Modifiee",
              "maskedNumber": "**** **** **** 4821",
              "provider": "LA_POSTE",
              "notes": "Notes modifiees"
            }
            """;

        mockMvc.perform(put("/api/cards/" + cardId)
                .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
                .content(updatePayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", equalTo("Carte 01 Modifiee")))
            .andExpect(jsonPath("$.notes", equalTo("Notes modifiees")));

            mockMvc.perform(put("/api/cards/" + cardId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content("""
                    {
                      "provider": "NOUVEAU_PROVIDER"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Carte 01 Modifiee")))
                .andExpect(jsonPath("$.maskedNumber", equalTo("**** **** **** 4821")))
                .andExpect(jsonPath("$.provider", equalTo("NOUVEAU_PROVIDER")))
                .andExpect(jsonPath("$.notes", equalTo("Notes modifiees")));

        // 5. PATCH /api/cards/{id}/status
        String statusPayload = """
            {
              "status": "BLOCKED"
            }
            """;

        mockMvc.perform(patch("/api/cards/" + cardId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
                .content(statusPayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", equalTo("BLOCKED")));
    }

    @Test
    void protectedRoutesRequireJwtAndSwaggerIsPublic() throws Exception {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        mockMvc.perform(get("/api/cards")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.type", equalTo("http")))
            .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme", equalTo("bearer")))
            .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.bearerFormat", equalTo("JWT")));
    }

            @Test
            @WithMockUser
            void rejectsCurrencyLongerThanThreeLetters() throws Exception {
            mockMvc.perform(post("/api/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Invalid currency",
                      "provider": "provider",
                      "currency": "dinar",
                      "initialBalance": 10
                    }
                    """))
                .andExpect(status().isBadRequest());
            }
}
