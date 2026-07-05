package com.library.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.common.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    /**
     * Atomically update available copies
     * Returns 0 if the update would make available_copies negative
     */
    @Update("UPDATE t_book SET available_copies = available_copies + #{delta} " +
            "WHERE id = #{id} AND available_copies + #{delta} >= 0 " +
            "AND total_copies >= available_copies + #{delta}")
    int updateStock(@Param("id") Long id, @Param("delta") int delta);
}
