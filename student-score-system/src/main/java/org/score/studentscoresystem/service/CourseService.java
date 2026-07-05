package org.score.studentscoresystem.service;

import org.score.studentscoresystem.entity.Course;
import org.score.studentscoresystem.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseMapper courseMapper;

    public Course getCourseById(Long id) {
        return courseMapper.selectById(id);
    }

    public List<Course> getAllCourses() {
        return courseMapper.selectAll();
    }

    public List<Course> getCoursesByTeacher(Long teacherId) {
        return courseMapper.selectByTeacherId(teacherId);
    }

    public List<Course> searchByCourseName(String courseName) {
        return courseMapper.selectByCourseName(courseName);
    }

    public List<Course> searchBySemester(String semester) {
        return courseMapper.selectBySemester(semester);
    }

    public boolean createCourse(Course course) {
        course.setStatus(1);
        return courseMapper.insert(course) > 0;
    }

    public boolean updateCourse(Course course) {
        return courseMapper.update(course) > 0;
    }

    public boolean deleteCourse(Long id) {
        return courseMapper.deleteById(id) > 0;
    }
}
