package com.zerobase.weather.controller;

import com.zerobase.weather.domain.Diary;
import com.zerobase.weather.service.DiaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController // 컨트롤러에서 상태 코드를 지정을 해서 내려줄 수 있게끔 해주는 컨트롤러
public class DiaryController {

	private final DiaryService diaryService;

	@ApiOperation(value = "일기 텍스트와 날씨를 이용해서 DB에 일기 저장합니다.", notes = "이건 노트 ") // 한줄 설명
	@PostMapping ("/create/diary") // 요청을 보낼 때 넣어주는 파라미터(날짜를 url 뒤에 파라미터로 요청할 수 있음),(바디에 데이터 넣어서 전송)
	void createDiary(@RequestParam @DateTimeFormat(iso= ISO.DATE) LocalDate date, @RequestBody String text) {
		// 받은 값들을 서비스에 전달
		diaryService.createDiary(date, text);

	}

	// 일기 조회
	@ApiOperation(value = "선택한 날짜의 모든 일기 데이터를 가져옵니다.", notes = "이건 노트 ")
	@GetMapping("/read/diary")
	List<Diary> readDiary(@RequestParam  @DateTimeFormat(iso= ISO.DATE) LocalDate date) {
		// 요청 파라미터로 넘어와야 하는 건 해당 날짜의 일기를 조회하는 것이므로 date 필요.
		return diaryService.readDiary(date);
	}
	// 일기 조회 - 2. 조회 날짜 범위 지정하기
	@ApiOperation(value = "선택한 기간 중의 모든 일기 데이터를 가져옵니다.", notes = "이건 노트 ")
	@GetMapping("/read/diaries")
	List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso= ISO.DATE)
							@ApiParam(value = "조회할 기간의 첫번 째 날", example = "2022-11-30")LocalDate startDate,
							@RequestParam @DateTimeFormat(iso= ISO.DATE)
							@ApiParam(value = "조회할 기간의 마지막 날", example = "2022-11-30") LocalDate endDate) {
		return  diaryService.readDiaries(startDate, endDate);

	}

	// 일기 수정
	@ApiOperation(value = "입력 날짜에 따른 일기를 수정할 수 있습니다.", notes = "이건 노트 ")
	@PutMapping("/update/diary") // 어떤 날짜의 일기를 수정할 것인지 ? (param) , 새로 작성할 값은 ? (RequestBody)
	void updateDiary(@RequestParam @DateTimeFormat(iso= ISO.DATE) LocalDate date,
						@RequestBody String text) {

		diaryService.updateDiary(date,text);
	}

	// 일기 삭제
	@ApiOperation(value = "선택 날짜의 일기를 삭제합니다.", notes = "이건 노트 ")
	@DeleteMapping("/delete/diary")
	void deleteDiary(@RequestParam @DateTimeFormat(iso= ISO.DATE) LocalDate date) {

		diaryService.deleteDiary(date);
	}

}
