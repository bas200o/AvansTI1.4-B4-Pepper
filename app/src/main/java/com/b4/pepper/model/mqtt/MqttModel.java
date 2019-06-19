package com.b4.pepper.model.mqtt;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttModel extends IntentService {

    private static final String TAG = "TI14-MQTT";

    public MqttModel() {

        super(MqttModel.class.getSimpleName());

        Log.d(TAG, "MqttMessageService()");
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.d(TAG, "onCreate");

        MqttClient mqttClient = new MqttClient();

        MqttAndroidClient client = mqttClient.getMqttClient(getApplicationContext(), MqttBuilder.MQTT_BROKER_URL, MqttBuilder.MQTT_CLIENT_ID);
        client.setCallback(new MqttCallbackExtended() {

            @Override
            public void connectComplete(boolean b, String s) {

                Log.i(TAG, "connectComplete()");
            }

            @Override
            public void connectionLost(Throwable throwable) {

                Log.i(TAG, "connectionLost()");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {

                String payload = new String( mqttMessage.getPayload() );

                try {

                    Intent broadCastIntent = new Intent();
                    broadCastIntent.setAction(MqttBuilder.MQTT_BROADCAST_ACTION);
                    broadCastIntent.putExtra("payload", payload);
                    sendBroadcast(broadCastIntent);

                } catch (Exception ex) {

                    ex.printStackTrace();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                Log.i(TAG, "deliveryComplete()");
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand()");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
