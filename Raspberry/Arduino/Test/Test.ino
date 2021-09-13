#include <Boards.h>
#include <Firmata.h>

//Endschalter
int sensePin3 = 2;
int wert3 = 0;


// the setup function runs once when you press reset or power the board
void setup() {
    Serial.begin(9600);  
    Serial.println("start");
}

// the loop function runs over and over again forever
void loop() {
   wert3 = analogRead(sensePin3);
   Serial.println(wert3);
   
}


