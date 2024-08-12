package com.levels.ShiftSync.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
	private Integer employee_id;
	private String name;
	private String email;
	private String department;
	private String phoneNumber;
	private LocalDateTime dateOfBirth;
}
