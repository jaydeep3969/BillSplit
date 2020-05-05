package com.demo.nikunj.splitwise;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 23/3/18.
 */

public class Transaction implements Serializable {
    String description;
    Float total;
    String date;
    String id;

    public String getGrpkey() {
        return grpkey;
    }

    public void setGrpkey(String grpkey) {
        this.grpkey = grpkey;
    }

    String grpkey="";

    public String getGrpname() {
        return grpname;
    }

    public void setGrpname(String grpname) {
        this.grpname = grpname;
    }

    String grpname="";
    Map<String,UserUID> userlist=new HashMap<>();

    public Transaction(String description, Float total, String date, String id) {
        this.description = description;
        this.total = total;
        this.date = date;
        this.id = id;
    }

    public Transaction() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, UserUID> getUserlist() {
        return userlist;
    }

    public void setUserlist(Map<String, UserUID> userlist) {
        this.userlist = userlist;
    }
}
