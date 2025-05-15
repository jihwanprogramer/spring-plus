package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoFindByCaseResponse;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepositoryCustom {

    Optional<Todo> getTodoByIdWithUser(Long id);

    Page<Todo> findByWeatherAndModifiedAtQuery(String weather, LocalDateTime startDate, LocalDateTime endDate,
                                               Pageable pageable);

    Page<TodoFindByCaseResponse> findByQueryDSL(String title, LocalDateTime startDate, LocalDateTime endDate, String nickname,
                                                Pageable pageable);
}
