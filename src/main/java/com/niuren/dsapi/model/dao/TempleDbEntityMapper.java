package com.niuren.dsapi.model.dao;


import com.niuren.dsapi.model.Entity.TempleDbEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TempleDbEntityMapper {

    int deleteByPrimaryKey(Integer sourceCode);

    int insert(TempleDbEntity record);

    int insertSelective(TempleDbEntity record);

    TempleDbEntity selectByPrimaryKey(Integer sourceCode);

    /**
     * @param record
     * @return
     */
    List<TempleDbEntity> selectListByType(TempleDbEntity record);

    int updateByPrimaryKeySelective(TempleDbEntity record);

    int updateByPrimaryKey(TempleDbEntity record);
}