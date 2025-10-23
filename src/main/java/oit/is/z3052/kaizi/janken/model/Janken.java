package oit.is.z3052.kaizi.janken.model;

public class Janken {
  // CPUは常にグー
  public String getCpuHand() {
    return "Gu";
  }

  // 勝敗判定
  public String judge(String user, String cpu) {
    if (user.equals(cpu)) {
      return "あいこ";
    } else if ((user.equals("Gu") && cpu.equals("Choki")) ||
        (user.equals("Choki") && cpu.equals("Pa")) ||
        (user.equals("Pa") && cpu.equals("Gu"))) {
      return "勝ち";
    } else {
      return "負け";
    }
  }
}
