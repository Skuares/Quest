package com.skuares.studio.quest;

import com.firebase.client.Firebase;
import com.shaded.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by salim on 12/19/2015.
 */
public class ToDo implements Serializable{

    private String desc;
    private String time;
    private double money;
    private double usersFinishedThisTodo;
    private APlace aPlace;
    private Map<String,Object> participants;

    public ToDo(){}


    public ToDo(@JsonProperty("desc") String desc,
                @JsonProperty("time") String time,
                @JsonProperty("money") double money,
                @JsonProperty("aPlace") APlace aPlace,
                @JsonProperty("participants") Map<String,Object> participants,
                @JsonProperty("usersFinishedThisTodo") double usersFinishedThisTodo){

        this.desc = desc;
        this.time = time;
        this.money = money;
        this.aPlace = aPlace;
        this.participants = participants;
        this.usersFinishedThisTodo = usersFinishedThisTodo;
    }

    public APlace getaPlace() {
        return aPlace;
    }

    public Map<String, Object> getParticipants() {
        return participants;
    }

    public double getUsersFinishedThisTodo() {
        return usersFinishedThisTodo;
    }

    public void increaseUsersFinishedThisTodo(){
        this.usersFinishedThisTodo++;
    }

    public void addParticipant(String userId, Firebase questRef,String index){
        if(userId != null && questRef != null){
            // I want to do this for every todos entry 0,1,2,...

            // insert locally
            participants.put(userId,false); // false by default, means he has not finished yet
            // insert in firebase
            questRef.child("todos").child(index).child("participants").updateChildren(participants);
        }
    }

    public void updateParticipant(String userId, Firebase questRef,String index){
        if(userId != null && questRef != null){
            // I want to do this for every todos entry 0,1,2,...

            // insert locally
            participants.put(userId,true);
            // insert in firebase
            questRef.child("todos").child(index).child("participants").updateChildren(participants);
        }
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
