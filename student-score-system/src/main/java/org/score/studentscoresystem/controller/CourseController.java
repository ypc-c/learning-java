package org.score.studentscoresystem.controller;

import org.score.studentscoresystem.common.Result;
import org.score.studentscoresystem.entity.Course;
import org.score.studentscoresystem.service.CourseService;
import org.score.studentscoresystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
@CrossOrigin
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/list")
    public Result<List<Course>> getAllCourses() {
        return Result.success(courseService.getAllCourses());
    }

    @GetMapping("/my-courses")
    public Result<List<Course>> getMyCourses(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        return Result.success(courseService.getCoursesByTeacher(userId));
    }

    @GetMapping("/{id}")
    public Result<Course> getCourseById(@PathVariable Long id) {
        return Result.success(courseService.getCourseById(id));
    }

    @GetMapping("/search/name")
    public Result<List<Course>> searchByName(@RequestParam String courseName) {
        return Result.success(courseService.searchByCourseName(courseName));
    }

    @GetMapping("/search/semester")
    public Result<List<Course>> searchBySemester(@RequestParam String semester) {
        return Result.success(courseService.searchBySemester(semester));
    }

    @PostMapping("/create")
    public Result<?> createCourse(@RequestBody Course course) {
        boolean success = courseService.createCourse(course);
        return success ? Result.success() : Result.error("创建失败");
    }

    @PutMapping("/update")
    public Result<?> updateCourse(@RequestBody Course course) {
        boolean success = courseService.updateCourse(course);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteCourse(@PathVariable Long id) {
        boolean success = courseService.deleteCourse(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
