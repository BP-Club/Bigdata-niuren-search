package com.niuren.dsapi.model.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@Mapper
public interface MemDataMapper {

	/**
	 * 查询HashMap
	 * @param hashMap
	 * @return
	 */
	public Integer findeJobCount(Map hashMap);
}