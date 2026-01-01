package org.example.novel_backened.vote;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface VoteMapper {
    @Select("select count(1) from votes where novel_id = #{novelId} and user_id = #{userId}")
    int countUserVote(@Param("novelId") String novelId, @Param("userId") String userId);

    @Insert("insert into votes(id, user_id, novel_id, created_at) values(#{id}, #{userId}, #{novelId}, now())")
    void insertVote(@Param("id") String id, @Param("userId") String userId, @Param("novelId") String novelId);

    @Update("update novels set vote_count = vote_count + 1 where id = #{novelId}")
    void increaseVote(@Param("novelId") String novelId);

    @Delete("delete from votes where user_id = #{userId}")
    void deleteByUserId(@Param("userId") String userId);

    @Delete("delete from votes where novel_id = #{novelId}")
    void deleteByNovelId(@Param("novelId") String novelId);
}

