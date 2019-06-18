package com.b4.pepper.model;

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
    public int getTablesAvailable(int numberOfPeople) {
        // ToDo:
        // Check how many tables are available for the requested number of people
        return (numberOfPeople<10)?5:0;
    }
}
