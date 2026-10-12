package com.rankbook.api.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.Matchers.*;

public class SupabaseContractTest {

    private String baseApiUrl;
    private String anonKey;
    private String serviceRoleKey;
    private static final String LEADS_ENDPOINT = "/rest/v1/crm_leads";

    @BeforeClass
    public void setup() {
        baseApiUrl = System.getProperty("supabase.api.url", "http://127.0.0.1:54321");
        anonKey = System.getProperty("supabase.anon.key", "sb_publishable_ACJWlzQHlZjBrEguHvfOxg_3BJgxAaH");
        serviceRoleKey = System.getProperty("supabase.service.role.key", System.getenv("SUPABASE_SERVICE_ROLE_KEY"));

        if (serviceRoleKey == null || serviceRoleKey.isBlank()) {
            try {
                java.util.Properties props = new java.util.Properties();
                try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                    if (is != null) props.load(is);
                }
                serviceRoleKey = props.getProperty("supabase.service.role.key", "dummy_service_role_key");
            } catch (Exception e) {
                serviceRoleKey = "dummy_service_role_key";
            }
        }

        RestAssured.baseURI = baseApiUrl;
    }

    @Test(description = "Verify that fetching CRM leads conforms strictly to JSON Schema contract")
    public void testLeadsApiSchemaContract() {
        InputStream schemaStream = getClass().getClassLoader().getResourceAsStream("schemas/lead-schema.json");

        given()
            .header("apikey", anonKey)
            .header("Authorization", "Bearer " + anonKey)
            .accept(ContentType.JSON)
        .when()
            .get(LEADS_ENDPOINT + "?limit=5")
        .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(matchesJsonSchema(schemaStream));
    }

    @Test(description = "Edge Case: Invalid API Key must return 401 Unauthorized")
    public void testInvalidApiKeyRejection() {
        given()
            .header("apikey", "invalid_expired_token_xyz")
            .header("Authorization", "Bearer invalid_expired_token_xyz")
            .accept(ContentType.JSON)
        .when()
            .get(LEADS_ENDPOINT)
        .then()
            .log().ifValidationFails()
            .statusCode(401)
            .body("message", notNullValue());
    }

    @Test(description = "Edge Case: Insertion with invalid stage violates column constraint")
    public void testInvalidLeadStatusConstraint() {
        Map<String, Object> invalidPayload = new HashMap<>();
        invalidPayload.put("id", UUID.randomUUID().toString());
        invalidPayload.put("full_name", "Invalid Status QA Lead");
        invalidPayload.put("email", "contract.err." + System.currentTimeMillis() + "@rankbook.in");
        invalidPayload.put("phone", "9876543210");
        invalidPayload.put("lead_status", "INVALID_UNSUPPORTED_STAGE");

        given()
            .header("apikey", serviceRoleKey)
            .header("Authorization", "Bearer " + serviceRoleKey)
            .contentType(ContentType.JSON)
            .header("Prefer", "return=representation")
            .body(invalidPayload)
        .when()
            .post(LEADS_ENDPOINT)
        .then()
            .log().ifValidationFails()
            .statusCode(anyOf(is(400), is(422)));
    }
}