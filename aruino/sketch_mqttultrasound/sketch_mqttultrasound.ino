#include <PubSubClient.h>
#include <ArduinoJson.h>
#include "WiFi.h"

// CONFIG table
/********************************************************************/
const char* id          = "0";
const char* seats       = "8";
/*********************************************************************/

// CONFIG Pins
int ledredpin = 14;
int ledgreenpin = 15;

char* testString = "{\"ledColor\":{\"id\":\"0\",\"isAvailable\":\"0\",\"seats\":\"8\"}}";

int trigPin = 13;
int echoPin = 12;
long pulse;
int distance;

// CONFIG WIFI
const char* ssid        = "1";
const char* password    = "12345678";


//CONFIG MQTT
const char* mqtt_broker   = "51.254.217.43";
int         mqtt_port     = 1883;
const char* mqtt_topic    = "B4Pepper420";
const char* mqtt_username = "emon";
const char* mqtt_password = "uw2ELjAKrEUwqgLT";

WiFiClient wifiClient;
PubSubClient mqttClient("", 0, wifiClient);

void setup() 
{
  // Init I/O devices
  pinMode(ledredpin, OUTPUT);
  pinMode(ledgreenpin, OUTPUT);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);

  // Init serial port
  Serial.begin(19200);
  Serial.println("Pepper arduino comms via mqtt");

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


void loop() 
{
  if (WiFi.status() == WL_CONNECTED) {
    if( !mqttClient.connected() ) {
      mqtt_connect();
    } else {
      mqttClient.loop();
    }

    digitalWrite(trigPin, LOW);
    delayMicroseconds(5);
    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin, LOW);
  
    pulse = pulseIn(echoPin, HIGH);
    distance = pulse / 29 / 2;
    //Serial.println(distance);
  
    if(distance < 80){
      digitalWrite(ledredpin, true);
      digitalWrite(ledgreenpin, false);
      mqtt_pubish(false);
    }else{
      digitalWrite(ledgreenpin, true);
      digitalWrite(ledredpin, false);
      mqtt_pubish(true);
    }
  
  } else { 
    Serial.println("Geen WiFi verbinding !");
    delay(900);
  }
  delay(1000);
}


void mqtt_connect() 
{
  mqttClient.setClient(wifiClient);
  mqttClient.setServer(mqtt_broker, mqtt_port);

  // Connect with unique id
  String clientId = "PepperProject-";
  clientId += String(random(0xffff), HEX);
  clientId += '-';
  clientId += String((uint32_t)ESP.getEfuseMac(), HEX);
  
  if(mqttClient.connect( clientId.c_str(), mqtt_username, mqtt_password )){

    // Subscribe to topic
    mqttClient.subscribe(mqtt_topic);

    // Setup callback
    mqttClient.setCallback(mqtt_callback);
    Serial.printf("%s: Connected to %s:%d\n", __FUNCTION__, mqtt_broker, mqtt_port);
  } else {    
    Serial.printf("%s: Connection ERROR (%s:%d)\n", __FUNCTION__, mqtt_broker, mqtt_port);
    delay(2000);
  }
}


void mqtt_pubish(boolean isAvailable)
{
  Serial.printf("\ntopic:\n");
  Serial.printf(mqtt_topic);
  DynamicJsonDocument jsonDocument(100);

  JsonObject colors = jsonDocument.createNestedObject("ledColor");
  colors["id"] = id;
  colors["isAvailable"] = String(isAvailable?1:0);
  colors["seats"] = seats;

  char json[100];
//  strcpy(json, "{\"esp\": {\"id\": ");
//  strcat(json, 0);
//  strcat(json, "\"}}");
  
  serializeJson(jsonDocument, json);
  Serial.printf("\nPayload: ");
  Serial.printf(json);
  if(mqttClient.publish(mqtt_topic, json)){
    Serial.println("yay");
  }else{
    Serial.println("nay");
  }
}

String charArrayToString(char* arr)
{
    String line = "";
    int array_len = sizeof(arr)/sizeof(arr[0]);
    for (int i = 0; i < array_len; i++) {
      line += arr[i];
    }
    return line;
}

void mqtt_callback(char* topic, byte* payload, unsigned int length)
{
  
    if( 0 == strcmp(topic, mqtt_topic) ) {
    // Parse payload
    DynamicJsonDocument jsonDocument(1024);
    
    DeserializationError error = deserializeJson(jsonDocument, payload);
    if( !error ) {
      JsonVariant msg = jsonDocument["esp"];
      if(!msg.isNull()) {
        // Flits de blauwe led
        digitalWrite(ledredpin, false);
        delay(50);
        digitalWrite(ledredpin, true);
      }
    }
  }
}
