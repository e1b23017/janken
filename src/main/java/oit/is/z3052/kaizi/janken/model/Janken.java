package oit.is.z3052.kaizi.janken.model;

public class Janken {
  // CPUは常にグー
  public String getCpuHand() {
    return "グー";
  }

  // 勝敗判定
  public String judge(String user, String cpu) {
    if (user.equals(cpu)) {
      return "あいこ";
    } else if ((user.equals("グー") && cpu.equals("チョキ")) ||
        (user.equals("チョキ") && cpu.equals("パー")) ||
        (user.equals("パー") && cpu.equals("グー"))) {
      return "勝ち";
    } else {
      return "負け";
    }
  }
}
