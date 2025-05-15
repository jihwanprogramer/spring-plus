package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoFindByCaseResponse;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    //QueryDSL 방법: user id를 이용해 todo 조회

    @Override
    public Optional<Todo> getTodoByIdWithUser(Long id) {

        QTodo todo = QTodo.todo;
        QUser user =QUser.user;

        return Optional.ofNullable(jpaQueryFactory
            .selectFrom(todo)
            .leftJoin(todo.user, user).fetchJoin()
            .where(todo.id.eq(id))
            .fetchOne());
    }


    //QueryDSL 방법: Weather 조건 modified 조건 추가
    private BooleanExpression weatherEq(String weather) {
        QTodo todo = QTodo.todo;
        return weather != null && !weather.isEmpty() ? todo.weather.containsIgnoreCase(weather) : null;
    }

    private BooleanExpression startDateExp(LocalDateTime startDate) {
        QTodo todo = QTodo.todo;
        return startDate != null ? todo.modifiedAt.goe(startDate) : null;
    }

    private BooleanExpression endDateExp(LocalDateTime endDate) {
        QTodo todo = QTodo.todo;
        return endDate != null ? todo.modifiedAt.loe(endDate) : null;
    }

    private BooleanExpression titleEq(String title) {
        QTodo todo = QTodo.todo;
        return title != null && !title.isEmpty() ? todo.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression nicknameEq(String nickname) {
        QTodo todo = QTodo.todo;
        return nickname != null && !nickname.isEmpty() ? todo.user.nickname.containsIgnoreCase(nickname) : null;
    }

    @Override
    public Page<Todo> findByWeatherAndModifiedAtQuery(String weather, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(weatherEq(weather));
        builder.and(startDateExp(startDate));
        builder.and(endDateExp(endDate));

        List<Todo> content = jpaQueryFactory
            .selectFrom(todo)
            .leftJoin(todo.user, user).fetchJoin()
            .where(builder)
            .orderBy(todo.modifiedAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> totalCountQuery = jpaQueryFactory
            .select(todo.count())
            .from(todo)
            .leftJoin(todo.user, user).fetchJoin()
            .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, ()->totalCountQuery.fetch().size());

    }

    @Override
    public Page<TodoFindByCaseResponse> findByQueryDSL(String title, LocalDateTime startDate, LocalDateTime endDate, String nickname, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(titleEq(title));
        builder.and(startDateExp(startDate));
        builder.and(endDateExp(endDate));
        builder.and(nicknameEq(nickname));

        List<TodoFindByCaseResponse> content =jpaQueryFactory
            .select(Projections.constructor(TodoFindByCaseResponse.class,
                todo.title,
                todo.managers.size().longValue(),
                todo.comments.size().longValue()
            ))
            .from(todo)
            .leftJoin(todo.user,user)
            .orderBy(todo.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> totalCountQuery = jpaQueryFactory
            .select(todo.count())
            .from(todo)
            .leftJoin(todo.user, user)
            .where(builder);

        return PageableExecutionUtils.getPage(content,pageable,()->totalCountQuery.fetch().size());
    }


}

