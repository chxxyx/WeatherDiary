package com.zerobase.weather.domain;


import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor // 빈 객체를 먼저 만들어서 그 안에 값을 채우고 싶다면 사용, 반대로 한번에 다이어리의 모든 컬럼을 넣을 때는 AllArgs ~
public class Diary {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String weather;
	private String icon;
	private double temperature;
	private String text;
	private LocalDate date;

}
