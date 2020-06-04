package com.niuren.dsapi.model.dao;

import com.niuren.dsapi.model.Entity.TempleSqlEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TempleSqlEntityMapper {

    int deleteByPrimaryKey(Long templeCode);

    int insert(TempleSqlEntity record);

    int insertSelective(TempleSqlEntity record);

    TempleSqlEntity selectByPrimaryKey(Long templeCode);

    int updateByPrimaryKeySelective(TempleSqlEntity record);

    int updateByPrimaryKeyWithBLOBs(TempleSqlEntity record);

    int updateByPrimaryKey(TempleSqlEntity record);


    /**
     * 模糊查询
     *
     * @param record
     * @return
     */
    List<TempleSqlEntity> selectTempListByCon(TempleSqlEntity record);
}