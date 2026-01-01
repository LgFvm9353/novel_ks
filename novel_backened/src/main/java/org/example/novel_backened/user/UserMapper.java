package org.example.novel_backened.user;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select id, username, password, role from users where username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("select id, username, password, role from users where id = #{id}")
    User findById(@Param("id") String id);

    @Insert("insert into users(id, username, password, role, created_at) values(#{id}, #{username}, #{password}, #{role}, now())")
    @Options(useGeneratedKeys = false)
    void insert(User user);

    @Update("update users set role = #{role} where id = #{id}")
    void updateRole(@Param("id") String id, @Param("role") String role);

    @Select("select count(1) from users")
    long countAll();

    @Select("select count(1) from users where role = #{role}")
    long countByRole(@Param("role") String role);

    @Select("select id, username, role, created_at as createdAt from users order by created_at desc")
    List<org.example.novel_backened.admin.UserAdminDto> findAllForAdmin();

    @Delete("delete from users where id = #{id}")
    void deleteById(@Param("id") String id);
}
