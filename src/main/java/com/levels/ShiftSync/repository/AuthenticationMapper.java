package com.levels.ShiftSync.repository;

import org.apache.ibatis.annotations.Mapper;

import com.levels.ShiftSync.entity.Authentication;

@Mapper
public interface AuthenticationMapper {
	// ユーザー名でログイン情報を取得します。
	Authentication selectByEmployeeId(Integer employeeId);
}
