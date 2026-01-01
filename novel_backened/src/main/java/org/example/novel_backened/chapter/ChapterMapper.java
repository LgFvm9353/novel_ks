package org.example.novel_backened.chapter;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ChapterMapper {
    @Select("select chapter_number as chapterNumber, title from chapters where novel_id = #{novelId} order by chapter_number asc")
    List<ChapterSummaryDto> findByNovelId(@Param("novelId") String novelId);

    @Select("select novel_id as novelId, chapter_number as chapterNumber, title, content from chapters where novel_id = #{novelId} and chapter_number = #{chapterNumber}")
    ChapterDetailDto findDetail(@Param("novelId") String novelId, @Param("chapterNumber") int chapterNumber);

    @Select("select coalesce(max(chapter_number), 0) from chapters where novel_id = #{novelId}")
    int findMaxChapterNumber(@Param("novelId") String novelId);

    @Insert("insert into chapters(id, novel_id, chapter_number, title, content, created_at, updated_at) " +
            "values(#{id}, #{novelId}, #{chapterNumber}, #{title}, #{content}, now(), now())")
    void insert(ChapterRecord record);

    @Update("update chapters set title = #{title}, content = #{content}, updated_at = now() " +
            "where novel_id = #{novelId} and chapter_number = #{chapterNumber}")
    int update(ChapterRecord record);

    @Delete("delete from chapters where novel_id = #{novelId} and chapter_number = #{chapterNumber}")
    int delete(@Param("novelId") String novelId, @Param("chapterNumber") int chapterNumber);

    @Delete("delete from chapters where novel_id = #{novelId}")
    int deleteByNovelId(@Param("novelId") String novelId);
}
