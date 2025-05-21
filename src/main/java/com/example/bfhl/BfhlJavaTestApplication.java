package com.example.bfhl;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BfhlJavaTestApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BfhlJavaTestApplication.class, args);
    }

    @Override
    public void run(String... args) {
        RestTemplate restTemplate = new RestTemplate();

        String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "John Doe");
        requestBody.put("regNo", "REG12347");
        requestBody.put("email", "john@example.com");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(generateWebhookUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String webhookUrl = (String) response.getBody().get("webhookUrl");
            String accessToken = (String) response.getBody().get("accessToken");

            String finalQuery = "SELECT p.amount AS SALARY, " +
                "CONCAT(e.first_name, ' ', e.last_name) AS NAME, " +
                "TIMESTAMPDIFF(YEAR, e.dob, CURDATE()) AS AGE, " +
                "d.department_name AS DEPARTMENT_NAME " +
                "FROM payments p " +
                "JOIN employee e ON p.emp_id = e.emp_id " +
                "JOIN department d ON e.department = d.department_id " +
                "WHERE DAY(p.payment_time) != 1 " +
                "ORDER BY p.amount DESC " +
                "LIMIT 1;";

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            Map<String, String> answerBody = new HashMap<>();
            answerBody.put("finalQuery", finalQuery);

            HttpEntity<Map<String, String>> answerRequest = new HttpEntity<>(answerBody, headers);

            ResponseEntity<String> answerResponse = restTemplate.postForEntity(webhookUrl, answerRequest, String.class);

            System.out.println("Submission Response: " + answerResponse.getBody());
        } else {
            System.out.println("Failed to generate webhook. Response: " + response.getBody());
        }
    }
}
