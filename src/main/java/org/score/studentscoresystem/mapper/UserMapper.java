package org.score.studentscoresystem.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.score.studentscoresystem.entity.User;
import java.util.List;

@Mapper
public interface UserMapper {
    User selectByUsername(String username);
    User selectById(Long id);
    List<User> selectByRole(String role);
    List<User> selectByCourseId(Long courseId);
    int insert(User user);
    int update(User user);
    int deleteById(Long id);
    User selectByPhone(String phone);
}
