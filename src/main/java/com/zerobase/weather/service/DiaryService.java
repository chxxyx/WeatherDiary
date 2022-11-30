package com.zerobase.weather.service;
import com.zerobase.weather.domain.DateWeather;
import com.zerobase.weather.domain.Diary;
import com.zerobase.weather.repository.DateWeatherRepository;
import com.zerobase.weather.repository.DiaryRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DiaryService {

	private final DiaryRepository diaryRepository;
	private final DateWeatherRepository dateWeatherRepository;

	@Value("${openweathermap.key}")
	private String apiKey;

	public DiaryService(DiaryRepository diaryRepository,
		DateWeatherRepository dateWeatherRepository) {
		this.diaryRepository = diaryRepository;
		this.dateWeatherRepository = dateWeatherRepository;
	}

	// 시간 마다 날씨 데이터 저장 함수 (시간 대로 동작 하지만 pk이기 때문에 데이터는 한 번만 !)
	@Transactional
	@Scheduled(cron = "0 0 1 * * *") // 초 분 시 일  (0초 0분 1시, 매일, 매월)
	public void saveWeatherDate() {
		// 그날 1시에 대한 날씨데이터를 가져와야 함
		dateWeatherRepository.save(getWeatherFromApi());

	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void createDiary(LocalDate date, String text) {

		// 날씨 데이터 가져오기 (API or DB에서 가져오기)
		DateWeather dateWeather = getDateWeather(date); // 반복 되는 파싱 과정 생략 !

		// 파싱된 데이터 + 일기 값 우리 db에 넣기
		Diary nowDiary = new Diary();
		nowDiary.setDateWeather(dateWeather);

		nowDiary.setText(text);
		nowDiary.setDate(date);

		diaryRepository.save(nowDiary);

	}
	// 시간 마다 날씨 데이터 저장 함수를 위한 날씨 가져오는 함수
	private DateWeather getWeatherFromApi() {

		// open weather map에서 날씨 데이터 가져오기
		String weatherData = getWeatherString();

		// 받아온 날씨 json 파싱하기
		Map<String, Object> parsedWeather = parseWeather(weatherData);

		DateWeather dateWeather = new DateWeather();

		dateWeather.setDate(LocalDate.now()); // 현재의 날씨를 가져와야 하므로
		dateWeather.setWeather(parsedWeather.get("main").toString());
		dateWeather.setIcon(parsedWeather.get("icon").toString());
		dateWeather.setTemperature((Double) parsedWeather.get("temp"));

		return dateWeather;
	}

	// 날씨 데이터 가져오기
	private DateWeather getDateWeather(LocalDate date) {
		List<DateWeather> dateWeatherListFromDB =  dateWeatherRepository.findAllByDate(date);

		if (dateWeatherListFromDB.size() == 0) { // 만약 디비에 없다면 ?
			// 새로 api에서 날씨 정보 가져오기
			// 정책 상, 현재 날씨를 가져오도록 가정
			return getWeatherFromApi();
		}else {
			return dateWeatherListFromDB.get(0);
		}
	}


	// 조회 api
	@Transactional(readOnly = true)
	public List<Diary> readDiary(LocalDate date) {
		return diaryRepository.findAllByDate(date);
	}

	public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {

		return diaryRepository.findAllByDateBetween(startDate, endDate);

	}

	// 수정
	public void updateDiary(LocalDate date, String text) {
		// 동일한 날짜일 경우 첫번 째 일기를 수정하는 것으로 가정
		Diary nowDiary = diaryRepository.getFirstByDate(date);

		nowDiary.setText(text);  // text 요청, 수정

		diaryRepository.save(nowDiary); // 수정 사항 저장

	}

	// 삭제
	public void deleteDiary(LocalDate date) {
		diaryRepository.deleteAllByDate(date);
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


	private Map<String, Object> parseWeather(String jsonString) {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;

		// 파싱 작업이 정상적으로 작동하지 않는 경우를 핸들링 하기 위한 try catch 문

		try{
			jsonObject = (JSONObject) jsonParser.parse(jsonString);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		Map<String, Object> resultMap = new HashMap<>();


		JSONObject mainData = (JSONObject) jsonObject.get("main");
		resultMap.put("temp", mainData.get("temp"));

		JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
		JSONObject weatherData = (JSONObject) weatherArray.get(0); // 들어있는 객체가 한 개라 0번째 인덱스를 가져와야 함 !

		resultMap.put("main", weatherData.get("main"));
		resultMap.put("icon", weatherData.get("icon"));

		return resultMap;

	}
}
