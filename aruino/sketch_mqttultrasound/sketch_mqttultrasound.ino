/*-------------------------------------------------------------------------

Android to mqtt example for ESP32 based hardware. TI-1.4 Avans Breda

copyright may, 2019 dm.kroeske@avans.nl

Permission is hereby granted, free of charge, to any person obtaining a copy 
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights 
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is furnished
to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in 
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

-------------------------------------------------------------------------*/

#include <PubSubClient.h>
#include <ArduinoJson.h>
#include "WiFi.h"

const char* ssid        = "1";
const char* password    = "12345678";

const char* mqtt_broker   = "51.254.217.43";
const char* mqtt_topic    = "TI-14-2019/LED";
const char* mqtt_username = "emon";
const char* mqtt_password = "uw2ELjAKrEUwqgLT";


const int LED           = 14;  // Aanpassen!!
const int BTN           = 0;  // Aanpassen!!

WiFiClient wifiClient;
PubSubClient mqttClient("", 0, wifiClient);

/******************************************************************/
void setup()
/* 
short:      ESP8266 (Arduino) setup
inputs:        
outputs: 
notes:         
Version :   DMK, Initial code
*******************************************************************/
{

  // Init I/O devices
  pinMode(LED, OUTPUT);
  digitalWrite(LED, true);  // LED uit
  pinMode(BTN, INPUT);

  // Init serial port
  Serial.begin(115200);
  Serial.println("");
  Serial.println("TI-1.4 MQTT example for ESP32");

  // Enable WiFi en wacht op verbinding
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.print("");
  Serial.println("Wifi Connected: ");
  Serial.println(WiFi.localIP());
}

/******************************************************************/
void loop()
/* 
short:    ESP8266 (Arduino) main loop
inputs:   
outputs: 
notes:    
Version:  DMK, Initial code
*******************************************************************/
{
  if (WiFi.status() == WL_CONNECTED) {
    
    if( !mqttClient.connected() ) {
      mqtt_connect();
    } else {
      mqttClient.loop();
    }
  
    // Publish payload is BTN is pressed
    if( !digitalRead(BTN) ) {
      mqtt_pubish(
        random(100, 255), // Red value
        random(100, 255), // Green value
        random(100, 255)  // Blue value
      );
      delay(200);
    }
    
  } else { 
    Serial.println("Geen WiFi verbinding !");
    delay(1000);
  }
}

/******************************************************************/
void mqtt_connect() 
/* 
short:      Connect to MQTT server UNSECURE
inputs:        
outputs: 
notes:         
Version :   DMK, Initial code
*******************************************************************/
{  
  mqttClient.setClient(wifiClient);
  mqttClient.setServer(mqtt_broker, 1883);

  // Connect with unique id
  String clientId = "TI14-";
  clientId += String(random(0xffff), HEX);
  clientId += '-';
  clientId += String((uint32_t)ESP.getEfuseMac(), HEX);
  
  if(mqttClient.connect( clientId.c_str(), mqtt_username, mqtt_password )){

    // Subscribe to topic
    mqttClient.subscribe(mqtt_topic);

    // Setup callback
    mqttClient.setCallback(mqtt_callback);
    Serial.printf("%s: Connected to %s:%d\n", __FUNCTION__, mqtt_broker, 1883);
  } else {    
    Serial.printf("%s: Connection ERROR (%s:%d)\n", __FUNCTION__, mqtt_broker, 1883);
    delay(2000);
  }
}

/******************************************************************/
void mqtt_pubish(int r, int g, int b)
/* 
short:      Pulish on MQTT topic (UNSECURE)
inputs:        
outputs: 
notes:         
Version :   DMK, Initial code
*******************************************************************/
{
  DynamicJsonDocument jsonDocument(1024);

  JsonObject colors = jsonDocument.createNestedObject("ledColor");
  colors["r"] = r;
  colors["g"] = g;
  colors["b"] = b;

  char json[1024];
  serializeJson(jsonDocument, json);
  Serial.printf("%s\n", json);
  mqttClient.publish(mqtt_topic, json);
}

/******************************************************************/
void mqtt_callback(char* topic, byte* payload, unsigned int length)
/* 
short:    MQTT callback. Elke publish op subscibed topic wordt hier
          afgehandeld
inputs:   'topic' waarop gepublished is
          'payload' bevat de published datablock
          'length' is de lengte van het payload array
outputs: 
notes:    In deze callback wordt gebruikt gemaakt van JSON parser
Version:  DMK, Initial code
*******************************************************************/
{
  if( 0 == strcmp(topic, mqtt_topic) ) {
    // Parse payload
    DynamicJsonDocument jsonDocument(1024);
    DeserializationError error = deserializeJson(jsonDocument, payload);
    if( !error ) {
      JsonVariant msg = jsonDocument["ledColor"];
      if(!msg.isNull()) {
        // Flits de blauwe led
        digitalWrite(LED, false);
        delay(50);
        digitalWrite(LED, true);
      }
    }
  }
}
