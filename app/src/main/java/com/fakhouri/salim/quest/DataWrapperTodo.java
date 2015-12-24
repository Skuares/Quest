package com.fakhouri.salim.quest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by salim on 12/24/2015.
 */
public class DataWrapperTodo implements Serializable {


    private List<ToDo> toDos;

    public DataWrapperTodo(List<ToDo> toDos) {
        this.toDos = toDos;
    }

    public List<ToDo> getToDos() {
        return toDos;
    }
}
