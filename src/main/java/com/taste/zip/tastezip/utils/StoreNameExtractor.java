package com.taste.zip.tastezip.utils;

import com.taste.zip.tastezip.dto.CafeteriaDefaultDto;
import com.taste.zip.tastezip.entity.Cafeteria;
import com.taste.zip.tastezip.repository.CafeteriaRepository;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StoreNameExtractor {

    private CafeteriaRepository cafeteriaRepository;

    private final String apiKey;

    private final RestTemplate restTemplate;

    public StoreNameExtractor(String key, RestTemplateBuilder builder, CafeteriaRepository cafeteriaRepository) {
        this.apiKey = key;
        this.restTemplate = builder.build();
        this.cafeteriaRepository = cafeteriaRepository;
    }

    private List<String> readTitlesFromCSV(String filePath) {
        List<String> titles = new ArrayList<>();

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            titles = lines.skip(1) // Skip header row
                    .limit(20) // 타이틀 20개만 가져오기
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(titles.toArray().length);
        return titles;
    }

    public List<String> extractStoreNamesFromTitles() {
        List<String> titles = readTitlesFromCSV("src/main/resources/data/youtube_shorts.csv");
        List<String> guessedNames = new ArrayList<>();


        for (String title : titles) {
            String storeName = extractStoreNameFromTitle(title);
            storeName = storeName.replace("\n\n", ""); // \n\n 제거
            guessedNames.add(storeName);
            System.out.println(storeName);
        }

        return guessedNames;
    }
    public List<CafeteriaDefaultDto> findByCafeteriaName() {
        List<String> names = extractStoreNamesFromTitles();
        List<Cafeteria> cafeterias = cafeteriaRepository.findByNameIn(names);
        return cafeterias.stream()
                .map(CafeteriaDefaultDto::from)
                .collect(Collectors.toList());
    }

    private String extractStoreNameFromTitle(String title) {
        String prompt = "Extract only the store name from the following video title. It has less than 5 chars without '': " + title;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        headers.setBearerAuth(apiKey);

        OpenAIRequest request = new OpenAIRequest("gpt-3.5-turbo-instruct", prompt, 50, 1, null, 0.5);
        HttpEntity<OpenAIRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<OpenAIResponse> response = restTemplate.exchange(
                "https://api.openai.com/v1/completions",
                HttpMethod.POST,
                entity,
                OpenAIResponse.class
        );

        return response.getBody().getChoices().get(0).getText();
    }

    private static class OpenAIRequest {
        @JsonProperty("model")
        private final String model;
        @JsonProperty("prompt")
        private final String prompt;
        @JsonProperty("max_tokens")
        private final int maxTokens;
        @JsonProperty("n")
        private final int n;
        @JsonProperty("stop")
        private final Object stop;
        @JsonProperty("temperature")
        private final double temperature;

        public OpenAIRequest(String model, String prompt, int maxTokens, int n, Object stop, double temperature) {
            this.model = model;
            this.prompt = prompt;
            this.maxTokens = maxTokens;
            this.n = n;
            this.stop = stop;
            this.temperature = temperature;
        }
    }

    private static class OpenAIResponse {
        private List<Choice> choices;

        public List<Choice> getChoices() {
            return choices;
        }

        private static class Choice {
            private String text;

            public String getText() {
                return text;
            }
        }
    }
}

