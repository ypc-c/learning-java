package com.library.borrow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.common.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BorrowMapper extends BaseMapper<BorrowRecord> {

    /**
     * Find overdue records (BORROWED status and past due time)
     */
    @Select("SELECT * FROM t_borrow_record WHERE status = 'BORROWED' AND due_time < NOW()")
    List<BorrowRecord> findOverdueRecords();
}
