package com.rankbook.framework.utils;

import com.rankbook.framework.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class SupabaseUserSeeder {

    public static void createOrConfirmStudentUser(String email, String password) {
        String supabaseUrl = ConfigManager.get("supabase.api.url");
        String secretKey = ConfigManager.get("supabase.service.role.key");

        if (secretKey != null) {
            secretKey = secretKey.replace("\"", "").trim();
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("password", password);
        payload.put("email_confirm", Boolean.TRUE);

        try {
            Response response = RestAssured.given()
                .baseUri(supabaseUrl)
                .basePath("/auth/v1/admin/users")
                .header("apikey", secretKey)
                .header("Authorization", "Bearer " + secretKey)
                .contentType(ContentType.JSON)
                .body(payload)
                .relaxedHTTPSValidation()
                .when()
                .post();

            int code = response.getStatusCode();
            if (code == 200 || code == 201) {
                System.out.println("[Seeder] Successfully provisioned student: " + email);
            } else if (code == 422) {
                System.out.println("[Seeder] Student already exists in Supabase: " + email);
            } else {
                System.out.println("[Seeder] Admin endpoint status: " + code + ". Attempting regular signup fallback...");
                
                String anonKey = ConfigManager.get("supabase.anon.key");
                if (anonKey != null) {
                    anonKey = anonKey.replace("\"", "").trim();
                }
                
                RestAssured.given()
                    .baseUri(supabaseUrl)
                    .basePath("/auth/v1/signup")
                    .header("apikey", anonKey)
                    .header("Authorization", "Bearer " + anonKey)
                    .contentType(ContentType.JSON)
                    .body(payload)
                    .relaxedHTTPSValidation()
                    .when()
                    .post();
            }
        } catch (Exception e) {
            System.err.println("[Seeder] Seeding warning (non-fatal): " + e.getMessage());
        }
    }
}