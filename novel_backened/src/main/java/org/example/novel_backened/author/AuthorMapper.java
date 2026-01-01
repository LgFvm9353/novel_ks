package org.example.novel_backened.author;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuthorMapper {
    @Select("select id, user_id as userId, name, bio, created_at as createdAt from authors where user_id = #{userId}")
    Author findByUserId(@Param("userId") String userId);

    @Select("select id, user_id as userId, name, bio, created_at as createdAt from authors where id = #{id}")
    Author findById(@Param("id") String id);

    @Insert("insert into authors(id, user_id, name, bio, created_at) values(#{id}, #{userId}, #{name}, #{bio}, now())")
    void insert(Author author);

    @Delete("delete from authors where id = #{id}")
    void deleteById(@Param("id") String id);

    @Delete("delete from authors where user_id = #{userId}")
    void deleteByUserId(@Param("userId") String userId);

    @Select("select a.id, a.user_id as userId, a.name, a.bio, a.created_at as createdAt, u.username, u.role " +
            "from authors a join users u on a.user_id = u.id order by a.created_at desc")
    List<AuthorAdminDto> findAllWithUser();
}
