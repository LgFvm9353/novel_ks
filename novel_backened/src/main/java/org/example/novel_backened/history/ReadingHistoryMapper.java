package org.example.novel_backened.history;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ReadingHistoryMapper {
    @Select("select rh.id, rh.novel_id as novelId, rh.chapter_number as chapterNumber, rh.chapter_title as chapterTitle, rh.last_read_time as lastReadTime, " +
            "n.title as novelTitle, n.cover_image as coverImage " +
            "from reading_history rh " +
            "left join novels n on rh.novel_id = n.id " +
            "where rh.user_id = #{userId} order by rh.last_read_time desc")
    List<ReadingHistoryDto> findByUserId(@Param("userId") String userId);

    @Select("select id from reading_history where user_id = #{userId} and novel_id = #{novelId}")
    String findIdByUserAndNovel(@Param("userId") String userId, @Param("novelId") String novelId);

    @Insert("insert into reading_history(id, user_id, novel_id, chapter_number, chapter_title, last_read_time) " +
            "values(#{id}, #{userId}, #{novelId}, #{chapterNumber}, #{chapterTitle}, now())")
    void insert(ReadingHistory history);

    @Update("update reading_history set chapter_number = #{chapterNumber}, chapter_title = #{chapterTitle}, last_read_time = now() where id = #{id}")
    void update(ReadingHistory history);

    @Delete("delete from reading_history where id = #{id} and user_id = #{userId}")
    int deleteByIdAndUser(@Param("id") String id, @Param("userId") String userId);

    @Delete("delete from reading_history where user_id = #{userId}")
    int deleteAllByUser(@Param("userId") String userId);

    @Delete("delete from reading_history where novel_id = #{novelId}")
    int deleteByNovelId(@Param("novelId") String novelId);

    @Delete("delete from reading_history where novel_id = #{novelId} and chapter_number = #{chapterNumber}")
    int deleteByChapter(@Param("novelId") String novelId, @Param("chapterNumber") int chapterNumber);
}

