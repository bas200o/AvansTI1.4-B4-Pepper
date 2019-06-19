package com.b4.pepper.model.mqtt;

import com.b4.pepper.model.entity.ESPEntity;

interface MqttListener {

    void receiveESP(ESPEntity esp);
}
