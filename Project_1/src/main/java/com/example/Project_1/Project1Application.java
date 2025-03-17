package com.example.Project_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Project1Application {

	public static void main(String[] args) {
		SpringApplication.run(Project1Application.class, args);
		List<Integer> number = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
		number.stream()
				.filter(n->n % 2==0)
				.forEach(System.out::println);
	}
}
