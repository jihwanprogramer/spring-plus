package org.example.expert.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class WeatherDto {

    private String date;
    private String weather;

    public WeatherDto(String date, String weather) {
        this.date = date;
        this.weather = weather;
    }
}
