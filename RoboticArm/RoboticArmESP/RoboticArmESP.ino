#include <ArduinoOTA.h>
#include <ESP8266WebServer.h>
#include <SoftwareSerial.h>

ESP8266WebServer server(80);
SoftwareSerial serialWriter(3, 1); //Rx Tx

char* wifi_ssid = "";
char* wifi_pwd = "";
char* username = "";
char* password = "";

void handleAPI() {
  if (!server.authenticate(username, password)) {
    return server.requestAuthentication(DIGEST_AUTH);
  }
  serialWriter.print(server.arg(0));
  Serial.println(server.arg(0));
  server.send(200, "text/plain");
}

void setup() {
  Serial.begin(115200);
  serialWriter.begin(9600);

  WiFi.mode(WIFI_STA);
  WiFi.begin(wifi_ssid, wifi_pwd);

  Serial.print("Connecting to ");
  Serial.println(wifi_ssid);

  if (WiFi.waitForConnectResult() != WL_CONNECTED) {
    Serial.println("Connecting to WiFi failed. Rebooting...");
    delay(1000);
    ESP.restart();
  }

  ArduinoOTA.begin();

  server.on("/api", handleAPI);
  server.begin();

  Serial.print("WiFi connected. IP Address: ");
  Serial.println(WiFi.localIP());
}

void loop() {
  ArduinoOTA.handle();
  server.handleClient();
}
