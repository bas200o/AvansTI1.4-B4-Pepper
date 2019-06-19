package com.b4.pepper.model.mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.b4.pepper.AppActivity;
import com.b4.pepper.model.entity.ESPEntity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class MqttBuilder {

    // configuration
    public static final String MQTT_TOPIC = "dezeTopicNaamIsSpecifiekLangGemaaktOmAnderenWegTeHoudenVanOnzePrachtigePepperRobot";
    public static final String MQTT_CLIENT_ID = "MY_ID_" + UUID.randomUUID().toString();
    public static final String MQTT_BROKER_URL = "tcp://51.254.217.43:1883";
    public static final String MQTT_BROADCAST_ACTION = "com.b4.pepper";

    // attributes
    private MqttAndroidClient client;
    private MqttListener listener;
    private MqttClient mqttClient;

    public MqttBuilder(MqttListener listener) {


        this.mqttClient = new MqttClient();

        this.client = this.mqttClient.getMqttClient(AppActivity.getContext(), MQTT_BROKER_URL, MQTT_CLIENT_ID);

        this.listener = listener;

        this.start();
    }

    private void start() {

        try {

            Intent intent = new Intent(MqttBuilder.this, MqttModel.class);
            startService(intent);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void sendJson(boolean get, int id) {

        String json = "{\"get\": " + get + ", \"id\": " + id + "}";

        try {

            this.mqttClient.publishMessage(this.client, json, 0, MQTT_TOPIC);
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void subscribe() {

        try {

            this.mqttClient.subscribe(this.client, MQTT_TOPIC, 0);

        } catch (MqttException e) {

            e.printStackTrace();
        }
    }

    public void unsubscribe() {

        try {

            this.mqttClient.unSubscribe(this.client, MQTT_TOPIC);

        } catch (MqttException e) {

            e.printStackTrace();
        }
    }

    // Defineer een eigen broadcast receiver, deze vangt alles op voor
    public class MqttBroadcastReceiver extends BroadcastReceiver {

        // configuration
        private final String TAG = "MyBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                String payload = intent.getStringExtra("payload");
                Log.i(TAG, payload);

                try {

                    JSONObject jsonObject = new JSONObject(payload);

                    ESPEntity esp = new ESPEntity();
                    esp.setId(jsonObject.getInt("id"));
                    esp.setSeats(jsonObject.getInt("seats"));
                    esp.setAvailable(jsonObject.getBoolean("isAvailable"));

                    //listener.receiveData(esp);

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }
    }
}
