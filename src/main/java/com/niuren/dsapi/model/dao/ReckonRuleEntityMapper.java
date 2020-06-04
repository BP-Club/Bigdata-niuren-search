package com.niuren.dsapi.model.dao;

import com.niuren.dsapi.model.Entity.ReckonRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ReckonRuleEntityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ReckonRuleEntity record);

    int insertSelective(ReckonRuleEntity record);

    ReckonRuleEntity selectByPrimaryKey(Integer id);

    List<ReckonRuleEntity> queryReckonTaskListByCon(ReckonRuleEntity record);

    int updateByPrimaryKeySelective(ReckonRuleEntity record);

    int updateByPrimaryKeyWithBLOBs(ReckonRuleEntity record);

    int updateByPrimaryKey(ReckonRuleEntity record);
}