package com.shangzuo.veindemo;

public class VeinSave {
    private String userId;
    private String finger1;
    private String finger2;

    public VeinSave() {
    }

    public VeinSave(String userId, String finger ,boolean isFirst) {
        this.userId = userId;
        if (isFirst){
            this.finger1 = finger;
        }else {
            this.finger2 = finger;
        }
    }



    public VeinSave(String userId, String finger1, String finger2) {
        this.userId = userId;
        this.finger1 = finger1;
        this.finger2 = finger2;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFinger1() {
        return finger1;
    }

    public void setFinger1(String finger1) {
        this.finger1 = finger1;
    }

    public String getFinger2() {
        return finger2;
    }

    public void setFinger2(String finger2) {
        this.finger2 = finger2;
    }
}
