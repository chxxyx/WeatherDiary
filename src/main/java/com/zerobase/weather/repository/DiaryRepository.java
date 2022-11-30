package com.zerobase.weather.repository;

import com.zerobase.weather.domain.Diary;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository <Diary, Integer>{

	// 조회 함수
	List<Diary> findAllByDate(LocalDate date);

	// 날짜 범위 제한 조회 함수
	List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

	// 일기 수정
	Diary getFirstByDate(LocalDate date);

}
