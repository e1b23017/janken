package oit.is.z3052.kaizi.janken.controller;

import oit.is.z3052.kaizi.janken.model.Janken;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import oit.is.z3052.kaizi.janken.model.Entry;

@Controller
public class JankenController {

  @Autowired
  private Entry entry;

  // index.htmlからのGET(ユーザ名受け取り)
  @GetMapping("/janken")
  public String showJanken(
      @RequestParam(value = "hand", required = false) String userHand,
      Model model,
      Principal prin) {
    String username = prin.getName();
    System.out.print(username);

    entry.addUser(username);
    model.addAttribute("username", username);
    model.addAttribute("allUsers", entry.getUsers());

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
