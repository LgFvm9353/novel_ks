package org.example.novel_backened.comment;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Select("select c.id, c.user_id as userId, u.username, c.content, c.created_at as createdAt " +
            "from comments c left join users u on c.user_id = u.id " +
            "where c.novel_id = #{novelId} order by c.created_at desc")
    List<CommentDto> findByNovelId(@Param("novelId") String novelId);

    @Insert("insert into comments(id, novel_id, user_id, content, created_at) values(#{id}, #{novelId}, #{userId}, #{content}, now())")
    void insert(Comment comment);

    @Delete("delete from comments where id = #{id} and user_id = #{userId}")
    int deleteByIdAndUser(@Param("id") String id, @Param("userId") String userId);

    @Delete("delete from comments where user_id = #{userId}")
    int deleteByUserId(@Param("userId") String userId);

    @Delete("delete from comments where novel_id = #{novelId}")
    int deleteByNovelId(@Param("novelId") String novelId);
}

