package oit.is.z3052.kaizi.janken.controller;

import java.util.ArrayList;
import java.util.List;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.z3052.kaizi.janken.model.Janken;
import oit.is.z3052.kaizi.janken.model.MatchMapper;
import oit.is.z3052.kaizi.janken.model.User;
import oit.is.z3052.kaizi.janken.model.UserMapper;
import oit.is.z3052.kaizi.janken.model.Match;
import oit.is.z3052.kaizi.janken.model.MatchInfo;
import oit.is.z3052.kaizi.janken.model.MatchInfoMapper;

@Controller
public class JankenController {

  // Entry の代わりに DB を参照する UserMapper を注入
  @Autowired
  private UserMapper userMapper;

  @Autowired
  private MatchMapper matchMapper;

  @Autowired
  private MatchInfoMapper matchInfoMapper;

  // index.htmlからのGET(ユーザ名受け取り)
  @GetMapping("/janken")
  public String showJanken(
      @RequestParam(value = "hand", required = false) String userHand,
      Model model,
      Principal prin) {

    String username = (prin != null) ? prin.getName() : null;

    if (username == null) {
      model.addAttribute("username", null);
      model.addAttribute("allUsers", new ArrayList<User>());
      return "janken";
    }

    // ログインユーザが users テーブルに存在しなければ追加する (初回のみ)
    User existing = userMapper.selectByName(username);
    if (existing == null) {
      User newUser = new User();
      newUser.setName(username);
      userMapper.insertUser(newUser);
      existing = userMapper.selectByName(username);
    }

    // DB から全ユーザを取得してテンプレに渡す
    ArrayList<User> users = userMapper.selectAll();

    model.addAttribute("username", username);
    model.addAttribute("allUsers", users);

    // DB から全試合を取得してテンプレへ渡す (ArrayListを利用)
    ArrayList<Match> matches = matchMapper.selectAll();
    model.addAttribute("allMatches", matches);

    // ログインユーザに関連する isActive=true の matchinfo を取得してテンプレに渡す
    try {
      if (existing != null) {
        List<MatchInfo> activeMatches = matchInfoMapper.selectActiveMatchesByUserId(existing.getId());
        model.addAttribute("activeMatchInfos", activeMatches);
      } else {
        model.addAttribute("activeMatchInfos", new ArrayList<MatchInfo>());
      }
    } catch (Exception e) {
      System.err.println("failed to load active matchinfo: " + e.getMessage());
      model.addAttribute("activeMatchInfos", new ArrayList<MatchInfo>());
    }

    if (userHand != null) {
      Janken janken = new Janken();
      String cpuHand = janken.getCpuHand();
      String result = janken.judge(userHand, cpuHand);

      model.addAttribute("userHand", userHand);
      model.addAttribute("cpuHand", cpuHand);
      model.addAttribute("result", result);
    }

    return "janken";
  }

  @GetMapping("/match")
  public String showMatch(@RequestParam("id") Integer id, Model model, Principal prin) {
    String loginName = (prin != null) ? prin.getName() : null;

    // ログインユーザの User オブジェクト
    User loginUser = null;
    if (loginName != null) {
      try {
        loginUser = userMapper.selectByName(loginName);
      } catch (Exception e) {
        System.err.println("failed to load login user: " + e.getMessage());
      }
    }

    // 対戦相手ユーザを id から取得
    User opponent = null;
    try {
      opponent = userMapper.selectById(id);
    } catch (Exception e) {
      System.err.println("failed to load opponent user id=" + id + " : " + e.getMessage());
    }

    // Model に入れる
    model.addAttribute("loginUser", loginUser);
    model.addAttribute("opponent", opponent);

    return "match";
  }

  @GetMapping("/fight")
  public String doFight(@RequestParam("id") Integer opponentId,
      @RequestParam("hand") String userHand,
      Model model,
      Principal prin) {
    String loginName = (prin != null) ? prin.getName() : null;
    if (loginName == null) {
      model.addAttribute("error", "ログイン情報が見つかりません。");
      return "match";
    }

    // ログインユーザと対戦相手を取得
    User loginUser = userMapper.selectByName(loginName);
    User opponent = userMapper.selectById(opponentId);

    if (loginUser == null) {
      model.addAttribute("error", "ログインユーザが見つかりません。");
      return "match";
    }
    if (opponent == null) {
      model.addAttribute("error", "対戦相手が見つかりません。");
      return "match";
    }

    Janken janken = new Janken();
    String cpuHand = janken.getCpuHand();

    // 判定
    String result = janken.judge(userHand, cpuHand);

    // DB に保存する
    try {
      Match newMatch = new Match();
      newMatch.setUser2(opponent.getId());
      newMatch.setUser1(loginUser.getId());
      newMatch.setUser2Hand(userHand);
      newMatch.setUser1Hand(cpuHand);
      matchMapper.insertMatch(newMatch);
    } catch (Exception e) {
      System.err.println("failed to insert match: " + e.getMessage());
      model.addAttribute("dbError", "試合情報の保存に失敗しました。");
    }

    // 表示用に model に詰める
    model.addAttribute("loginUser", loginUser);
    model.addAttribute("opponent", opponent);
    model.addAttribute("userHand", userHand);
    model.addAttribute("cpuHand", cpuHand);
    model.addAttribute("result", result);

    return "match";
  }

  @GetMapping("/wait")
  public String showWait(@RequestParam("id") Integer opponentId,
      @RequestParam("hand") String userHand,
      Model model,
      Principal prin) {
    String loginName = (prin != null) ? prin.getName() : null;
    if (loginName == null) {
      model.addAttribute("error", "ログイン情報が見つかりません。");
      return "match";
    }

    User loginUser = userMapper.selectByName(loginName);
    User opponent = userMapper.selectById(opponentId);

    if (loginUser == null) {
      model.addAttribute("error", "ログインユーザが見つかりません。");
      return "match";
    }
    if (opponent == null) {
      model.addAttribute("error", "対戦相手が見つかりません。");
      return "match";
    }

    try {
      // 既に自分が参加する is_active=true の matchinfo があれば挿入しない
      List<MatchInfo> myActive = matchInfoMapper.selectActiveMatchesByUserId(loginUser.getId());
      if (myActive == null || myActive.isEmpty()) {
        // まだ自分のアクティブ試合がない -> 挿入
        MatchInfo mi = new MatchInfo();
        mi.setUser1(loginUser.getId());
        mi.setUser2(opponent.getId());
        mi.setUser1Hand(userHand);
        mi.setIsActive(true);
        matchInfoMapper.insertMatchInfo(mi);
      } else {
        // すでに参加中のアクティブ試合がある場合は挿入を行わない（別の挙動が必要ならここを調整）
      }
    } catch (Exception e) {
      System.err.println("failed to insert matchinfo in /wait: " + e.getMessage());
      model.addAttribute("dbError", "試合情報の保存に失敗しました。");
    }

    // 表示に必要な情報を渡す
    model.addAttribute("loginUser", loginUser);
    model.addAttribute("opponent", opponent);

    return "wait";
  }

  // ルートパスにアクセスした場合はindex.html(静的)を表示
  @GetMapping("/")
  public String index() {
    return "redirect:/index.html";
  }
}
