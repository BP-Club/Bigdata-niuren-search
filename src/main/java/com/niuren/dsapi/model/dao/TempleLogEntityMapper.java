package com.niuren.dsapi.model.dao;


import com.niuren.dsapi.model.Entity.TempleLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TempleLogEntityMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TempleLogEntity record);

    int insertSelective(TempleLogEntity record);

    TempleLogEntity selectByPrimaryKey(Long id);


    /**
     * 根据模板id 查询日志
     *
     * @param record
     * @return
     */
    List<TempleLogEntity> selectListByTempleCode(TempleLogEntity record);

    int updateByPrimaryKeySelective(TempleLogEntity record);

    int updateByPrimaryKey(TempleLogEntity record);
}