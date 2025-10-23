package oit.is.z3052.kaizi.janken.model;

public class Match {
  private Integer id;
  private Integer user1;
  private Integer user2;
  private String user1Hand;
  private String user2Hand;

  private String user1Name;
  private String user2Name;

  public Match() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getUser1() {
    return user1;
  }

  public void setUser1(Integer user1) {
    this.user1 = user1;
  }

  public Integer getUser2() {
    return user2;
  }

  public void setUser2(Integer user2) {
    this.user2 = user2;
  }

  public String getUser1Hand() {
    return user1Hand;
  }

  public void setUser1Hand(String user1Hand) {
    this.user1Hand = user1Hand;
  }

  public String getUser2Hand() {
    return user2Hand;
  }

  public void setUser2Hand(String user2Hand) {
    this.user2Hand = user2Hand;
  }

  public String getUser1Name() {
    return user1Name;
  }

  public void setUser1Name(String user1Name) {
    this.user1Name = user1Name;
  }

  public String getUser2Name() {
    return user2Name;
  }

  public void setUser2Name(String user2Name) {
    this.user2Name = user2Name;
  }
}
