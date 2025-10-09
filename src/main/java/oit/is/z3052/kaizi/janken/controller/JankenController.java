package oit.is.z3052.kaizi.janken.controller;

import oit.is.z3052.kaizi.janken.model.Janken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class JankenController {

  // index.htmlからのGET(ユーザ名受け取り)
  @GetMapping("/janken")
  public String showJanken(@RequestParam(value = "username", required = false) String username,
      @RequestParam(value = "hand", required = false) String userHand,
      Model model) {
    if (username != null) {
      model.addAttribute("username", username);
    }

    if (userHand != null) {
      // じゃんけん処理
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
