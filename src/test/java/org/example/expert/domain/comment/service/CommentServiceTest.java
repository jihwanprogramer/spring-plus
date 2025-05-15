package org.example.expert.domain.comment.service;

import jakarta.persistence.EntityManager;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void getComments() {
        User[] users = new User[5];
        for (int i = 0; i < 5; i++) {
            users[i] = userRepository.save(new User("test" + i + "@example.com", "password", UserRole.USER, "테스트" + i));
        }
        Todo todo = todoRepository.save(new Todo("제목", "내용", "맑음", users[0]));

        for (int i = 0; i < 5; i++) {
            Comment comment = new Comment("댓글 " + i, users[i], todo);
            commentRepository.save(comment);
        }

        List<CommentResponse> comments = commentService.getComments(todo.getId());
        System.out.println("댓글 개수: " + comments.size());

        assertEquals(5, comments.size(), "댓글 개수가 5개여야 합니다.");
    }


}
