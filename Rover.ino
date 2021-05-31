#include <Servo.h>

Servo myservo;

//Replace these with your own pins
int redPin = 6;
int greenPin = 5;
int bluePin = 3;
int buttonPin = 8;
int photoPin = A0;
int joyPin = A1;

int redVal = 255;
int blueVal = 255;
int greenVal = 255;
void setup() {
  myservo.attach(2);
  myservo.write(90);
  Serial.begin(9600);
  pinMode(photoPin, INPUT);
  pinMode(joyPin, INPUT);
  pinMode(buttonPin, INPUT_PULLUP);
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT);
  pinMode(10, OUTPUT);
  pinMode(11, OUTPUT);
  pinMode(12, OUTPUT);
  pinMode(13, OUTPUT);
  Serial.println("Ready");
}
void color(int r, int g, int b) {
  analogWrite(redPin, r);
  analogWrite(greenPin, g);
  analogWrite(bluePin, b);
}
String data = "";
void loop() {
  if (Serial.available() > 0) {
    // read the incoming bytes:
    data = Serial.readString();
    if(data.substring(0,1) == "c") {
       int val  = data.substring(1,4).toInt();
       redVal = val;
       greenVal = val;
       blueVal = val;
    } 
  }
  int y_pos = analogRead(joyPin);
  int servo_val = map(y_pos,0,1023,0,180);
  myservo.write(servo_val);
  int photoVal = analogRead(photoPin);
  if(photoVal < 45) {
    Serial.println("p1");
    color(redVal, blueVal, greenVal);
  } else {
    Serial.println("p0");
    color(0,0,0);
  }
  int buttonVal = digitalRead(buttonPin);
  Serial.println("b" + String(buttonVal));
  if(buttonVal == 0) {
    drill();
  }
}
void drill() {
  digitalWrite(10, HIGH);
  digitalWrite(11, LOW);
  digitalWrite(12, LOW);
  digitalWrite(13, LOW);
  delay(5);
  digitalWrite(10, LOW);
  digitalWrite(11, HIGH);
  digitalWrite(12, LOW);
  digitalWrite(13, LOW);
  delay(5);
  digitalWrite(10, LOW);
  digitalWrite(11, LOW);
  digitalWrite(12, HIGH);
  digitalWrite(13, LOW);
  delay(5);
  digitalWrite(10, LOW);
  digitalWrite(11, LOW);
  digitalWrite(12, LOW);
  digitalWrite(13, HIGH);
  delay(5);
}
