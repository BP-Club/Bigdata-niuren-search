package com.niuren.dsapi.model.dao;


import com.niuren.dsapi.model.Entity.PushTaskHisEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * push task
 */
@Repository
@Mapper
public interface PushTaskHisEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PushTaskHisEntity record);

    int insertSelective(PushTaskHisEntity record);

    PushTaskHisEntity selectByPrimaryKey(Long id);

    /**
     * 根据taskId是否存在
     *
     * @param taskId
     * @return
     */
    PushTaskHisEntity selectByTaskId(String taskId);

    int updateByPrimaryKeySelective(PushTaskHisEntity record);

    int updateByPrimaryKey(PushTaskHisEntity record);
}