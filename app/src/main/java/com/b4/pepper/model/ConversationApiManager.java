package com.b4.pepper.model;

import com.b4.pepper.model.entity.ESPEntity;
import com.b4.pepper.model.mqtt.TableManager;

public class ConversationApiManager {
    private static ConversationApiManager instance;
    public static ConversationApiManager getInstance(){
        if (instance == null){
            instance = new ConversationApiManager();
        }
        return instance;
    }
    private ConversationApiManager(){
        // SingleTon
    }

    /**
     * Finds and returns a table for the given number of people
     * @param numberOfPeople the number of people that would like a single table
     * @return the id of the found table. Or -1 if there's no table available
     */
    public int getTableForGuests(int numberOfPeople) {
        TableManager tableManager = new TableManager();
        tableManager.reserveTable(numberOfPeople);
        ESPEntity table = tableManager.getPickedTable();
        if (table != null){
            return table.getId();
        }
        else {
            return -1;
        }
    }
}
