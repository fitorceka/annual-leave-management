package com.lhind.annualleavemanagement.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    @PostMapping("/weather")
    public String getWeatherData(@RequestParam("city") String city, Model model) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest
            .newBuilder()
            .uri(URI.create("https://open-weather13.p.rapidapi.com/city/tirana"))
            .header("X-RapidAPI-Key", "f4c15a6bffmsh0994b6a2f797943p154dadjsn97d654dc1fdd")
            .header("X-RapidAPI-Host", "open-weather13.p.rapidapi.com")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
