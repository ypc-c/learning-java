package org.score.studentscoresystem.controller;

import org.score.studentscoresystem.common.Result;
import org.score.studentscoresystem.entity.User;
import org.score.studentscoresystem.service.UserService;
import org.score.studentscoresystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserById(userId);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<?> updateUser(@RequestBody User user) {
        boolean success = userService.updateUser(user);
        return success ? Result.success() : Result.error("更新失败");
    }

    @GetMapping("/teachers")
    public Result<List<User>> getTeachers() {
        return Result.success(userService.getTeachers());
    }

    @GetMapping("/students")
    public Result<List<User>> getStudents() {
        return Result.success(userService.getStudents());
    }

    @GetMapping("/students/course/{courseId}")
    public Result<List<User>> getStudentsByCourse(@PathVariable Long courseId) {
        return Result.success(userService.getStudentsByCourse(courseId));
    }

    @PostMapping("/create")
    public Result<?> createUser(@RequestBody User user) {
        boolean success = userService.createUser(user);
        return success ? Result.success() : Result.error("创建失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
