package com.example.whatsmytask.models;

public class Friend {

        private String idUser1;
        private String idUser2;

        public Friend(){}
    public Friend(String idUser1, String idUser2) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
    }

    public String getIdUser1() {
        return idUser1;
    }

    public void setIdUser1(String idUser1) {
        this.idUser1 = idUser1;
    }

    public String getIdUser2() {
        return idUser2;
    }

    public void setIdUser2(String idUser2) {
        this.idUser2 = idUser2;
    }
}
