package org.score.studentscoresystem.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.score.studentscoresystem.entity.Score;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ScoreMapper {
    Score selectById(Long id);
    Score selectByCourseAndStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
    List<Score> selectByStudentId(Long studentId);
    List<Score> selectByCourseId(Long courseId);
    List<Score> selectByCondition(@Param("courseId") Long courseId, 
                                   @Param("keyword") String keyword,
                                   @Param("minScore") BigDecimal minScore,
                                   @Param("maxScore") BigDecimal maxScore);
    int insert(Score score);
    int update(Score score);
    int deleteById(Long id);
}
