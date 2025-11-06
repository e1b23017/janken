package oit.is.z3052.kaizi.janken.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import oit.is.z3052.kaizi.janken.model.MatchMapper;
import oit.is.z3052.kaizi.janken.model.MatchInfoMapper;
import oit.is.z3052.kaizi.janken.model.UserMapper;
import oit.is.z3052.kaizi.janken.model.Match;
import oit.is.z3052.kaizi.janken.model.MatchInfo;
import oit.is.z3052.kaizi.janken.model.User;
import oit.is.z3052.kaizi.janken.model.Janken;

@RestController
public class AsyncKekka {

  @Autowired
  UserMapper userMapper;

  @Autowired
  MatchMapper matchMapper;

  @Autowired
  MatchInfoMapper matchInfoMapper;

  // 非同期ポーリング用エンドポイント
  // Principal からユーザを判定し、matches テーブルに is_active = true の試合があれば結果を返す
  @GetMapping("/async-kekka")
  public Map<String, Object> checkKekka(Principal prin) {
    Map<String, Object> res = new HashMap<>();
    String login = (prin != null) ? prin.getName() : null;
    if (login == null) {
      res.put("found", false);
      return res;
    }

    User loginUser = userMapper.selectByName(login);
    if (loginUser == null) {
      res.put("found", false);
      return res;
    }

    try {
      // matches テーブルに is_active=true のレコードがあるか確認
      Match activeMatch = matchMapper.selectActiveMatchByUserId(loginUser.getId());
      if (activeMatch == null) {
        res.put("found", false);
        return res;
      }

      // マッチが見つかった -> 判定を計算して返す
      String userHand;
      String opponentHand;
      String result;

      if (loginUser.getId().equals(activeMatch.getUser1())) {
        userHand = activeMatch.getUser1Hand();
        opponentHand = activeMatch.getUser2Hand();
        result = new Janken().judge(userHand, opponentHand);
      } else {
        userHand = activeMatch.getUser2Hand();
        opponentHand = activeMatch.getUser1Hand();
        result = new Janken().judge(userHand, opponentHand);
      }

      // レスポンス作成
      res.put("found", true);
      res.put("matchId", activeMatch.getId());
      res.put("userHand", userHand);
      res.put("opponentHand", opponentHand);
      res.put("result", result);

      // 試合を検出したら matches の is_active を false にして、対応する matchinfo も false にする
      // matches を非アクティブにする
      activeMatch.setIsActive(false);
      matchMapper.updateIsActiveById(activeMatch);

      // さらに、対応する matchinfo を探して is_active=false にする
      MatchInfo mi = matchInfoMapper.selectActiveBetweenUsers(activeMatch.getUser1(), activeMatch.getUser2());
      if (mi != null) {
        mi.setIsActive(false);
        matchInfoMapper.updateIsActiveById(mi);
      }

      return res;
    } catch (Exception e) {
      System.err.println("async-kekka error: " + e.getMessage());
      res.put("found", false);
      return res;
    }
  }
}
