#include <PubSubClient.h>
#include <ArduinoJson.h>
#include "WiFi.h"

// CONFIG table
/********************************************************************/
int  id                 = 1;
const char* seats       = "8";
/*********************************************************************/

// CONFIG Pins
int ledredpin = 14;
int ledgreenpin = 15;

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
  
  digitalWrite(ledgreenpin, true);
}


void loop() 
{
  if (WiFi.status() == WL_CONNECTED) {
    digitalWrite(trigPin, LOW);
    delayMicroseconds(5);
    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin, LOW);
  
    pulse = pulseIn(echoPin, HIGH);
    distance = pulse / 29 / 2;
 
    if( !mqttClient.connected() ) {
      mqtt_connect();
    } else {
      mqttClient.loop();
    }

  } else { 
    Serial.println("Geen WiFi verbinding !");
    delay(900);
  }
  delay(1000);
}

//checkt of de tafel beschikbaar is en stuurt via mqtt de beschikbaarheid
void checkAndSendStatus(){
      if(distance < 80){
      digitalWrite(ledredpin, true);
      digitalWrite(ledgreenpin, false);
      mqtt_pubish(false);
    }else{
      digitalWrite(ledgreenpin, true);
      digitalWrite(ledredpin, false);
      mqtt_pubish(true);
    }
}

//reserveerd de tafel en laat voor 10 seconden oranje branden en daarna rood.
void reserve(){
      digitalWrite(ledgreenpin, true);
      digitalWrite(ledredpin, true);
      delay(10000);
      digitalWrite(ledgreenpin, false);
}

//verbind de esp met de mqttserver
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

//stuurt naar de mqtt server de beschikbaarheid
void mqtt_pubish(boolean isAvailable)
{
  Serial.printf(mqtt_topic);
  DynamicJsonDocument jsonDocument(100);

  //JsonObject info = jsonDocument.createNestedObject("");
  jsonDocument["id"] = id;
  jsonDocument["isAvailable"] = isAvailable;
  jsonDocument["seats"] = seats;

  char json[100];
  serializeJson(jsonDocument, json);
  Serial.printf("\nPayload: ");
  Serial.printf(json);
  mqttClient.publish(mqtt_topic, json);
}

//kijkt voor nieuwe berichten in de mqtt server en parced de berichten voor ons
void mqtt_callback(char* topic, byte* payload, unsigned int length)
{
  
    Serial.println("\n\nA new mqtt message");
    if( 0 == strcmp(topic, mqtt_topic) ) {
    // Parse payload
    DynamicJsonDocument jsonDocument(1024);
    
    DeserializationError error = deserializeJson(jsonDocument, payload);
    if( !error ) {
        int jsonGet = jsonDocument["get"];
        Serial.print("jsonGet got a: ");
        Serial.print(jsonGet);
        if(jsonGet == 1){
                
           Serial.print(jsonGet);
           checkAndSendStatus();
                
        }else if(jsonGet == 2){
                
           int jsonID = jsonDocument["id"];
           Serial.println("\nid ");
           Serial.print(jsonID);
                
           if(jsonID == id){
               reserve();
           }
        }else{
            Serial.print("This message cant be parced here.");
        }
    }else{
        Serial.println("error reading message");
    }
  }
}
