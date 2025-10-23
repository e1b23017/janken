package oit.is.z3052.kaizi.janken.controller;

import java.util.ArrayList;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.z3052.kaizi.janken.model.Janken;
import oit.is.z3052.kaizi.janken.model.User;
import oit.is.z3052.kaizi.janken.model.UserMapper;

@Controller
public class JankenController {

  // Entry の代わりに DB を参照する UserMapper を注入
  @Autowired
  private UserMapper userMapper;

  // index.htmlからのGET(ユーザ名受け取り)
  @GetMapping("/janken")
  public String showJanken(
      @RequestParam(value = "hand", required = false) String userHand,
      Model model,
      Principal prin) {

    String username = prin.getName();
    System.out.println("login username: " + username);

    // ログインユーザが users テーブルに存在しなければ追加する (初回のみ)
    User existing = userMapper.selectByName(username);
    if (existing == null) {
      User newUser = new User();
      newUser.setName(username);
      userMapper.insertUser(newUser);
    }

    // DB から全ユーザを取得してテンプレに渡す
    ArrayList<User> users = userMapper.selectAll();
    System.out.println("users.size() = " + users.size());

    model.addAttribute("username", username);
    model.addAttribute("allUsers", users);

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

  // ルートパスにアクセスした場合はindex.html(静的)を表示
  @GetMapping("/")
  public String index() {
    return "redirect:/index.html";
  }
}
