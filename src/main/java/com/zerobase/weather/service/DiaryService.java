package com.zerobase.weather.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DiaryService {

	@Value("${openweathermap.key}")
	private String apiKey;
	public void createDiary(LocalDate date, String text) {

		// open weather map에서 날씨 데이터 가져오기
	String weatherData = getWeatherString();

		// 받아온 날씨 json 파싱하기

		// 파싱된 데이터 + 일기 값 우리 db에 넣기

	}



	private String getWeatherString() {

		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

		try {
			URL url = new URL(apiUrl);

			// apiUrl을 http 형식으로 호출
			HttpURLConnection connection = (HttpURLConnection)  url.openConnection(); // 요청을 보낼 수 있는 커넥션을 열었음
			connection.setRequestMethod("GET"); // get 요청
			int responseCode = connection.getResponseCode(); // 응답 코드를 받음

			BufferedReader br; // BufferedReader 안에 해당 응답 코드들을 넣어둠
			if (responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			}
			// BufferedReader에 넣어두었던 코드들을 읽으면서 (BufferedReader - 속도 빠름, 성능 향상)
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine); // StringBuilder에 결과값들을 쌓아줌
			}

			br.close();
			return response.toString();

		} catch (Exception e) {
			return "failed to get Response";
		}

	}

}