#include <Servo.h>
#include <ArduinoJson.h>
#include <SoftwareSerial.h>
#include <string.h>
#include <Wire.h>  
#include <LiquidCrystal_I2C.h>

#define PIN_CLAW 2
#define PIN_ELBOW 3
#define PIN_BASE 4

#define PIN_SELECTOR 5
#define PIN_CW 6
#define PIN_CCW 7

#define PIN_LED_AUTO 8
#define PIN_LED_MAN 9

#define PIN_SWITCH 13

#define CLAW_MAX 50
#define ELBOW_MAX 75
#define BASE_MAX 180

#define DELAY 20

#define PIN_RX 0
#define PIN_TX 1

LiquidCrystal_I2C lcd(0x27, 16, 2);
SoftwareSerial serialReader(PIN_RX, PIN_TX);
Servo sClaw, sElbow, sBase;
int clawPos, elbowPos, basePos;

bool isAutomatic;
byte state, servoSelector_pressed, modeSelector_pressed;

void parseJSON(String json) {
  Serial.println(json);

  const char s[2] = "\n";
  char * cstr = new char[json.length() + 1];
  strcpy(cstr, json.c_str());

  char *token = strtok(cstr, s);

  while (token != NULL) {
    StaticJsonBuffer<200> jsonBuffer;
    JsonObject& node = jsonBuffer.parseObject(token);

    if (node.containsKey("Delay")) {
      rotateAutomatic(node["Claw"], node["Elbow"], node["Base"]);
      delay(node["Delay"]);
    } else {
      rotateAutomatic(node["Claw"], node["Elbow"], node["Base"]);
      delay(DELAY);
    }

    token = strtok(NULL, s);
  }
}

void rotateAutomatic(int clawVAL, int elbowVAL, int baseVAL) {
  Serial.println(clawVAL);
  Serial.println(elbowVAL);
  Serial.println(baseVAL);

  ClawRot(clawVAL);
  ElbowRot(elbowVAL);
  BaseRot(baseVAL);
}

void ClawRot(int pos) {
  if (pos > CLAW_MAX) pos = CLAW_MAX;
  else if (pos < 0) pos = 0;

  if (pos > clawPos) {
    for (int i = clawPos; i <= pos; i++) {
      sClaw.write(i);
      delay(DELAY);
    }
  } else if (pos < clawPos) {
    for (int i = clawPos; i >= pos; i--) {
      sClaw.write(i);
      delay(DELAY);
    }
  }
  clawPos = pos;
}

void ElbowRot(int pos) {
  if (pos > ELBOW_MAX) pos = ELBOW_MAX;
  else if (pos < 0) pos = 0;

  if (pos > elbowPos) {
    for (int i = elbowPos; i <= pos; i++) {
      sElbow.write(i);
      delay(DELAY);
    }
  } else if (pos < elbowPos) {
    for (int i = elbowPos; i >= pos; i--) {
      sElbow.write(i);
      delay(DELAY);
    }
  }
  elbowPos = pos;
}

void BaseRot(int pos) {
  if (pos > BASE_MAX) pos = BASE_MAX;
  else if (pos < 0) pos = 0;

  if (pos > basePos) {
    for (int i = basePos; i <= pos; i++) {
      sBase.write(i);
      delay(DELAY);
    }
  } else if (pos < basePos) {
    for (int i = basePos; i >= pos; i--) {
      sBase.write(i);
      delay(DELAY);
    }
  }
  basePos = pos;
}

void rotateCC() {
  if (state == 0) {
    ClawRot(clawPos + 1);
  }
  else if (state == 1) {
    ElbowRot(elbowPos + 1);
  }
  else if (state == 2) {
    BaseRot(basePos + 1);
  }
}

void rotateCCW() {
  if (state == 0) {
    ClawRot(clawPos - 1);
  }
  else if (state == 1) {
    ElbowRot(elbowPos - 1);
  }
  else if (state == 2) {
    BaseRot(basePos - 1);
  }
}

void resetAll() {
  clawPos = 0;
  elbowPos = 0;
  basePos = 0;

  sClaw.write(clawPos);
  sElbow.write(elbowPos);
  sBase.write(basePos);
}

void writeLCD(String line1, String line2) {
  lcd.setCursor(0,0);
  lcd.print(line1);
  lcd.setCursor(0,1);
  lcd.print(line2);
}

void changeMode() {
  if (isAutomatic == HIGH) {
    digitalWrite(PIN_LED_MAN, LOW);
    digitalWrite(PIN_LED_AUTO, HIGH);
    writeLCD("Bratso", "IP:192.168.1.11");
  }
  else {
    digitalWrite(PIN_LED_MAN, HIGH);
    digitalWrite(PIN_LED_AUTO, LOW);

    if (state == 0) {
      writeLCD("Bratso", "Claw Selected");
    }
    else if (state == 1) {
      writeLCD("Bratso", "Elbow Selected");
    }
    else if (state == 2) {
      writeLCD("Bratso", "Base Selected");
    }
  }
}

void setup() {
  Serial.begin(9600);
  serialReader.begin(9600);

  pinMode(PIN_LED_AUTO, OUTPUT);
  pinMode(PIN_LED_MAN, OUTPUT);

  pinMode(PIN_SELECTOR, INPUT);
  pinMode(PIN_CW, INPUT);
  pinMode(PIN_CCW, INPUT);
  pinMode(PIN_SWITCH, INPUT);

  sClaw.attach(PIN_CLAW);
  sElbow.attach(PIN_ELBOW);
  sBase.attach(PIN_BASE);

  isAutomatic = LOW;
  resetAll();
}

void loop() {
  changeMode();
  if (digitalRead(PIN_SWITCH) == LOW)
  {
    modeSelector_pressed = 1;
    Serial.println("*");
  }
  else
  {
    if (modeSelector_pressed == 1) {
      modeSelector_pressed = 2;
    }
    Serial.println("$");
  }

  if (modeSelector_pressed == 2) {
    if (isAutomatic == HIGH) {
      isAutomatic = LOW;
    }
    else {
      isAutomatic = HIGH;
    }
    modeSelector_pressed = 0;
  }
    
  if (isAutomatic == HIGH) {
    if (serialReader.available()) {
      parseJSON(serialReader.readString());
    }
  }
  else {
    if (digitalRead(PIN_SELECTOR) == LOW)
    {
      servoSelector_pressed = 1;
    }
    else
    {
      if (servoSelector_pressed == 1) {
        servoSelector_pressed = 2;
      }
    }

    if (servoSelector_pressed == 2) {
      state = (state > 1) ? 0 : state + 1;
      servoSelector_pressed = 0;
    }

    if (digitalRead(PIN_CW) == LOW) {
      rotateCC();
    }
    else if (digitalRead(PIN_CCW) == LOW) {
      rotateCCW();
    }
  }

  delay(DELAY);
}
