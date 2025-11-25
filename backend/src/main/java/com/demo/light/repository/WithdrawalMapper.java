package com.demo.light.repository;

import com.demo.light.bean.Withdrawal;
import com.demo.light.enums.WithdrawalStatus;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WithdrawalMapper {
    //查找待处理的提现
    List<Withdrawal> findPending();

    //根据id和txId去更新status
    void updateStatus(Long id, WithdrawalStatus status,String txId);


    //插入record记录
    void insertRecord(Withdrawal record);

    //更新withdrawal
    void updateRecordById(Withdrawal withdrawal);
}
