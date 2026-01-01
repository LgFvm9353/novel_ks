package org.example.novel_backened.novel;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Select("select id, name from categories order by name asc")
    List<Category> findAll();

    @Select("select id, name from categories where id = #{id}")
    Category findById(@Param("id") String id);

    @Insert("insert into categories(id, name) values(#{id}, #{name})")
    void insert(Category category);
}
