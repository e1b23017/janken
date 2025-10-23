package oit.is.z3052.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchMapper {

  @Select("SELECT m.id, m.user1, m.user2, m.user1Hand, m.user2Hand, "
      + "u1.name AS user1Name, u2.name AS user2Name "
      + "FROM matches m "
      + "LEFT JOIN users u1 ON m.user1 = u1.id "
      + "LEFT JOIN users u2 ON m.user2 = u2.id "
      + "WHERE m.id = #{id}")
  Match selectById(int id);

  @Select("SELECT m.id, m.user1, m.user2, m.user1Hand, m.user2Hand, "
      + "u1.name AS user1Name, u2.name AS user2Name "
      + "FROM matches m "
      + "LEFT JOIN users u1 ON m.user1 = u1.id "
      + "LEFT JOIN users u2 ON m.user2 = u2.id "
      + "ORDER BY m.id ASC")
  ArrayList<Match> selectAll();

  @Insert("INSERT INTO matches (user1, user2, user1Hand, user2Hand) "
      + "VALUES (#{user1}, #{user2}, #{user1Hand}, #{user2Hand})")
  @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
  void insertMatch(Match match);
}
