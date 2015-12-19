package com.fakhouri.salim.quest;

/**
 * Created by salim on 12/19/2015.
 */
public class ToDo {

    private String desc;
    private String time;
    private double money;

    public ToDo(String desc, String time, double money){

        this.desc = desc;
        this.time = time;
        this.money = money;
    }

    public String getDesc() {
        return desc;
    }

    public String getTime() {
        return time;
    }

    public double getMoney() {
        return money;
    }
}
