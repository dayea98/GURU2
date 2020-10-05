package com.example.sy925.myapplication.model;

public class Member {
    private String name;
    private int tel;
    private String nickname;
    private String id;
    private String pwd;

    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", tel=" + tel +
                ", nickname='" + nickname + '\'' +
                ", id='" + id + '\'' +
                ", pwd='" + pwd + '\'' +
                ", pwdQ='" + pwdQ + '\'' +
                ", pwdA='" + pwdA + '\'' +
                '}';
    }

    private String pwdQ;
    private String pwdA;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTel() {
        return tel;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwdQ() {
        return pwdQ;
    }

    public void setPwdQ(String pwdQ) {
        this.pwdQ = pwdQ;
    }

    public String getPwdA() {
        return pwdA;
    }

    public void setPwdA(String pwdA) {
        this.pwdA = pwdA;
    }


}
