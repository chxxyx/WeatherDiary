package com.zerobase.weather.repository;

import com.zerobase.weather.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaMemoRepository extends JpaRepository<Memo, Integer> { //<가져올 클래스, 클래스의 id 값 형식>

}
