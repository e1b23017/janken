package oit.is.z3052.kaizi.janken.model;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchInfoMapper {

  @Insert("INSERT INTO matchinfo(user1, user2, user1Hand, isActive) VALUES(#{user1}, #{user2}, #{user1Hand}, #{isActive})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertMatchInfo(MatchInfo matchInfo);

  @Select("SELECT id, user1, user2, user1Hand, isActive FROM matchinfo WHERE isActive = true AND (user1 = #{userId} OR user2 = #{userId})")
  List<MatchInfo> selectActiveMatchesByUserId(Integer userId);
}
