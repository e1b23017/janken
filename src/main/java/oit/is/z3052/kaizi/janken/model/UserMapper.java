package oit.is.z3052.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
  @Select("SELECT id, name FROM users WHERE id = #{id}")
  User selectById(int id);

  @Select("SELECT id, name FROM users ORDER BY id")
  ArrayList<User> selectAll();

  @Select("SELECT id, name FROM users WHERE name = #{name}")
  User selectByName(String name);

  @Insert("INSERT INTO users (name) VALUES (#{name})")
  @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
  void insertUser(User user);
}
