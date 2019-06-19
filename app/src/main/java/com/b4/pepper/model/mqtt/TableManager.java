package com.b4.pepper.model.mqtt;

import com.b4.pepper.model.entity.ESPEntity;

import java.util.ArrayList;

public class TableManager implements MqttListener {

    private static final int MAX_ESPS = 1;

    private MqttBuilder mqttBuilder;
    private ArrayList<ESPEntity> esps;
    private ESPEntity pickedTable;

    public TableManager() {

        this.mqttBuilder = new MqttBuilder(this);
        this.mqttBuilder.subscribe();

        this.esps = new ArrayList<>();
    }

    public void reserveTable(final int amountOfPersons) {

        this.mqttBuilder.sendJson(MqttBuilder.GET_STATE_READ, 0);
        gatherEsps();
        pickEsp(amountOfPersons);
    }

    public ESPEntity getPickedTable() {

        return this.pickedTable;
    }

    private void gatherEsps() {

        while (this.esps.size() < MAX_ESPS) {

            try {

                Thread.sleep(10);

            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
    }

    private void pickEsp(int amountOfPersons) {

        for (ESPEntity esp : this.esps) {

            if (esp.isAvailable() && esp.getSeats() >= amountOfPersons) {

                this.mqttBuilder.sendJson(MqttBuilder.GET_STATE_SET, esp.getId());
                this.pickedTable = esp;

                return;
            } else {

                this.pickedTable = null;
            }
        }
    }

    @Override
    public void receiveESP(ESPEntity esp) {

        this.esps.add(esp);
    }
}
