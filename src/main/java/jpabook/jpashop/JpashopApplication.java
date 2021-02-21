package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication	// 이 어노테이션이 있는 클래스의 '패키지'와 '패키지 하위'의 모든 클래스를 스캔해서 빈으로 등록한다.
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

}
