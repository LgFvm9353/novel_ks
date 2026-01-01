package org.example.novel_backened.novel;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NovelMapper {
    @Select("select n.id, n.title, n.description, n.cover_image, n.status, c.name as category_name, n.vote_count " +
            "from novels n left join categories c on n.category_id = c.id " +
            "order by n.updated_at desc limit #{limit} offset #{offset}")
    List<NovelListItemDto> findList(@Param("limit") int limit, @Param("offset") int offset);

    @Select("select n.id, n.title, n.description, n.cover_image, n.status, c.name as category_name, n.vote_count " +
            "from novels n left join categories c on n.category_id = c.id " +
            "order by n.vote_count desc limit #{limit}")
    List<NovelListItemDto> findRanking(@Param("limit") int limit);

    @Select("select count(1) from novels")
    long countAll();

    @Select("select n.id, n.title, n.description, n.cover_image, n.status, c.name as category_name, " +
            "n.total_chapters, n.total_pages, n.vote_count, n.author_id, a.user_id as author_user_id, n.created_at, n.updated_at " +
            "from novels n " +
            "left join categories c on n.category_id = c.id " +
            "left join authors a on n.author_id = a.id " +
            "where n.id = #{id}")
    NovelDetailDto findDetailById(@Param("id") String id);

    @Select("select n.id, n.title, n.description, n.cover_image, n.status, c.name as category_name, n.vote_count " +
            "from novels n left join categories c on n.category_id = c.id " +
            "where n.author_id = #{authorId} order by n.updated_at desc")
    List<NovelListItemDto> findByAuthor(@Param("authorId") String authorId);

    @Select("select author_id from novels where id = #{id}")
    String findAuthorIdById(@Param("id") String id);

    @Insert("insert into novels(id, title, author_id, category_id, description, cover_image, status, total_chapters, total_pages, vote_count, created_at, updated_at) " +
            "values(#{id}, #{title}, #{authorId}, #{categoryId}, #{description}, #{coverImage}, #{status}, #{totalChapters}, #{totalPages}, #{voteCount}, now(), now())")
    void insert(Novel novel);

    @Update("update novels set title = #{title}, category_id = #{categoryId}, description = #{description}, cover_image = #{coverImage}, status = #{status}, updated_at = now() where id = #{id}")
    void update(Novel novel);

    @Delete("delete from novels where id = #{id}")
    void deleteById(@Param("id") String id);
}
