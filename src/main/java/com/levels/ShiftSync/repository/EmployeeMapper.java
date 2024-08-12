package com.levels.ShiftSync.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.levels.ShiftSync.entity.Employee;

@Mapper
public interface EmployeeMapper {
	// 指定した従業員情報を取得する。
	Employee selectById(@Param("employeeId") Integer employeeId);
}
