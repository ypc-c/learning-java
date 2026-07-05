package org.score.studentscoresystem.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.score.studentscoresystem.entity.CourseStudent;
import java.util.List;

@Mapper
public interface CourseStudentMapper {
    int insert(CourseStudent courseStudent);
    int deleteByCourseAndStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
    List<CourseStudent> selectByCourseId(Long courseId);
    List<CourseStudent> selectByStudentId(Long studentId);
}
