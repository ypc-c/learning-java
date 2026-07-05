package org.score.studentscoresystem.service;

import org.score.studentscoresystem.entity.User;
import org.score.studentscoresystem.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user != null && user.getPassword().equals(password) && user.getStatus() == 1) {
            return user;
        }
        return null;
    }

    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public List<User> getTeachers() {
        return userMapper.selectByRole("TEACHER");
    }

    public List<User> getStudents() {
        return userMapper.selectByRole("STUDENT");
    }

    public List<User> getStudentsByCourse(Long courseId) {
        return userMapper.selectByCourseId(courseId);
    }

    public boolean createUser(User user) {
        user.setStatus(1);
        return userMapper.insert(user) > 0;
    }

    public boolean updateUser(User user) {
        return userMapper.update(user) > 0;
    }

    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    public User findByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    public boolean resetPassword(Long userId, String newPassword) {
        User user = new User();
        user.setId(userId);
        user.setPassword(newPassword);
        return userMapper.update(user) > 0;
    }
}
