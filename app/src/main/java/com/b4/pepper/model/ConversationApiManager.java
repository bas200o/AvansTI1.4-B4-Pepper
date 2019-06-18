package com.b4.pepper.model;

public class ConversationApiManager {
    private ConversationApiManager instance;
    public ConversationApiManager getInstance(){
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
        return 0;
    }
}
