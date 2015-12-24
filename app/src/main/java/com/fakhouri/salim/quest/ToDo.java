package com.fakhouri.salim.quest;

import com.shaded.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by salim on 12/19/2015.
 */
public class ToDo implements Serializable{

    private String desc;
    private String time;
    private double money;

    public ToDo(){}


    public ToDo(@JsonProperty("desc") String desc,
                @JsonProperty("time") String time,
                @JsonProperty("money") double money){

        this.desc = desc;
        this.time = time;
        this.money = money;
    }
    /*
    public ToDo(String desc, String time, double money){

        this.desc = desc;
        this.time = time;
        this.money = money;
    }
    */

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
