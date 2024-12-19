
package com.mypackage.entity;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private final String API_KEY = "c7711539ee71271ffa4e0a3c3fbc1e19";
    private final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q={city}&appid={apiKey}";

    public String getWeather(String city) {
        RestTemplate restTemplate = new RestTemplate();
        String url = BASE_URL.replace("{city}", city).replace("{apiKey}", API_KEY);
        return restTemplate.getForObject(url, String.class);
    }
}
