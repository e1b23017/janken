package oit.is.z3052.kaizi.janken.model;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MatchInfoMapper {

  @Insert("INSERT INTO matchinfo(user1, user2, user1Hand, isActive) VALUES(#{user1}, #{user2}, #{user1Hand}, #{isActive})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertMatchInfo(MatchInfo matchInfo);

  @Select("SELECT id, user1, user2, user1Hand, isActive FROM matchinfo WHERE isActive = true AND (user1 = #{userId} OR user2 = #{userId})")
  List<MatchInfo> selectActiveMatchesByUserId(Integer userId);

  @Select("SELECT id, user1, user2, user1Hand,isActive FROM matchinfo WHERE isActive = true AND ((user1 = #{u1} AND user2 = #{u2}) OR (user1 = #{u2} AND user2 = #{u1})) LIMIT 1")
  MatchInfo selectActiveBetweenUsers(Integer u1, Integer u2);

  @Update("UPDATE matchinfo SET isActive = #{isActive} WHERE id = #{id}")
  void updateIsActiveById(MatchInfo mi);
}
