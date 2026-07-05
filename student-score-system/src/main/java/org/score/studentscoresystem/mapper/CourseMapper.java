package org.score.studentscoresystem.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.score.studentscoresystem.entity.Course;
import java.util.List;

@Mapper
public interface CourseMapper {
    Course selectById(Long id);
    List<Course> selectAll();
    List<Course> selectByTeacherId(Long teacherId);
    List<Course> selectByCourseName(@Param("courseName") String courseName);
    List<Course> selectBySemester(@Param("semester") String semester);
    int insert(Course course);
    int update(Course course);
    int deleteById(Long id);
}
