#include <Boards.h>
#include <Firmata.h>

/*
  Hühnertür öffner 
*/
int maxOpen = 180; // Anzahl der Umdrheungen bis Tür offen
int auf = 0 ; // Tür ist zu
int pinLR = 12;
int pinPWM = 11;
int autom = 1; //Wird durch Sensor geschaltet
int maxInterations =0;//Anzahl der maximalen Durchläufe, bevor im AufZu eine Aenderung ergibt
int errorschliessen =0; //Ist der Wert 1, gab es ein Problem beim schliessen
int erroroeffnen =0; //Ist der Wert 1, gab es ein Problem beim oeffnen

//interne Lichtschranke 1
int sensePin1 = 0;
int wert1 = 0;
int sens1 = 0;
int sens1Changed = 0;


//interne Lichtschranke 2
int sensePin2 = 1;
int wert2 = 0;
int sens2 = 0;
int sens2Changed = 0;

//Endschalter
int sensePin3 = 2;
int wert3 = 0;

//Lichtsensor
int sensePin4 = 3;
int wert4 = 0;


int richtung = 0; /* 0=links, 1 = rechts*/
int umdrehung = 0;
int tag = 0;
int rechts = 0;

//Serieller input
char inData[20]; // Allocate some space for the string
char inChar=-1; // Where to store the character read
byte index = 0; // Index into array; where to store the character
int ret =0; //Returnvalue from Comp


int zaehleUmdrehungen(int richtung, int wert) {
  // richtung rechts=1 links=0
  int iteration = 0;
  while (1 == 1)
  {
    delay(10); //Vermeidet das Prellen
    if (iteration > 100)
    {
      if (richtung == 1) 
      {
       Serial.println("Error oeffnen");
      }
      else
      {
       Serial.println("Error schliessen");
      }
       return 1;
    }
    
     wert3 = analogRead(sensePin3);
     if (richtung == 0 && wert3 >= 500)
     {
       Serial.println("Minwert erreicht (Endschalter)");
      return 0;
     } 
    
    
    if ( richtung == 0 && umdrehung <= 0)
    {
      Serial.println("Minwert erreicht");
      return 0;
    }
    if ( richtung == 1 && umdrehung >= maxOpen)
    {
      Serial.println("Maxwert erreicht");
      return 0;
    }    
    if (richtung ==0 &&umdrehung < 20)
    {
      int drossel = umdrehung*10 +50;
      if (drossel > 150 && drossel < 256)
      {
        analogWrite(pinPWM, drossel);
      }     
    }    
    sens1Changed = 0;
    wert1 = analogRead(sensePin1);
    if ( wert1 > 500)
    {
      if (sens1 == 0)
      {
        sens1 = 1;
        sens1Changed = 1;
        if ( sens2 == 0)
        {
          Serial.println("rechts");
            if (iteration > maxInterations)
            {
             maxInterations = iteration;
            }
          iteration=0;  
          richtung = 1;
          tag = tag + 1;
          if (tag >= 4)
          {
            umdrehung = umdrehung + 1;
            tag = 0;
            Serial.println(umdrehung);
          }
        }
        else
        {
          Serial.println("links");
             if (iteration > maxInterations)
            {
             maxInterations = iteration;
            }
          iteration=0;  
          richtung = 0;
          tag = tag - 1;
          if (tag <= 0)
          {
            tag = 4;
            umdrehung = umdrehung - 1;
            Serial.println(umdrehung);
          }
        }
      }

    }
    else
    {
      if (sens1 == 1)
      {
        sens1 = 0;
        sens1Changed = 1;
      }
    }
    sens2Changed = 0;
    wert2 = analogRead(sensePin2);
    if ( wert2 > 500)
    {
      if (sens2 == 0)
      {
        sens2 = 1;
        sens2Changed = 1;
      }
    }
    else
    {
      if (sens2 == 1)
      {
        sens2 = 0;
        sens2Changed = 1;
      }
    }
    iteration++;
  }
  return 0;
}
void schliessen() {
  erroroeffnen=0;
  if (errorschliessen == 1 )
  {
    return;
  }
  Serial.println("Schalte auf Linkslauf");
  rechts = 0;
  digitalWrite(pinLR, LOW);
  analogWrite(pinPWM, 255);
  errorschliessen=zaehleUmdrehungen(0, 0);
  digitalWrite(pinLR, LOW);
  analogWrite(pinPWM, 0);
  auf = 0;
}

void oeffnen() {
  errorschliessen=0;
  if (erroroeffnen == 1 )
  {
    return;
  }
  Serial.println("Schalte auf Rechtslauf");
  rechts = 1;
  digitalWrite(pinLR, HIGH);
  analogWrite(pinPWM, 0);
  zaehleUmdrehungen(1, 40);
  digitalWrite(pinLR, LOW);
  analogWrite(pinPWM, 0);
  auf = 1;
}

void initialisiere() {
  wert2 = 0; 
  wert3 = analogRead(sensePin3);
  Serial.println("Initialisierung ....");
  if (wert3 >= 500)
 {
   return;
 } 
  digitalWrite(pinLR, LOW);
    analogWrite(pinPWM, 150);
  while ( wert3 < 500)
  {
    // Drehe links, bis Endschalter erreicht
    
    delay(20);
    wert3 = analogRead(sensePin3);
   
  }
   digitalWrite(pinLR, LOW);
    analogWrite(pinPWM, 0);
  umdrehung = 0;
  rechts = 0;
  Serial.println("Initialisierung done");
}

void liesLichtsensor() {
  int sum = 0;
  for (int i = 0; i < 10; i++)
  {
    //Serial.println("Liessensor");
    wert4 = analogRead(sensePin4);
    //Serial.println(wert4);
    sum = sum + wert4;
     delay(3000);
  }

  wert4 = sum / 10;
  Serial.println("Sensorwert:");
    Serial.println(wert4);
}

// the setup function runs once when you press reset or power the board
void setup() {

  // initialize digital pin 13 as an output.
  //initialisiere();
  pinMode(13, OUTPUT);
  Serial.begin(9600);
   initialisiere();
}


//Lies serial ein
int Comp(){
char funcToken [20];
 while(Serial.available() > 0) // Don't read unless
   // there you know there is data
 {
   if(index < 20) // One less than the size of the array
   {
     inChar = Serial.read(); // Read a character
     inData[index] = inChar; // Store it
     index++; // Increment where to write next
     inData[index] = '\0'; // Null terminate the string
   }
 }
Serial.println("Message received");
Serial.println(inData);
strncpy(funcToken, inData, 2); 
funcToken[2]='\0';
//Serial.println("funcToken");
//Serial.println(funcToken);
if (strcmp(funcToken,"mT")==0)
{
 char * wert = &inData [2];
 int newmaxOpen = atoi(wert);
  // printf ("Wert = %d",val);
  if ( maxOpen != newmaxOpen)
  {
    maxOpen=newmaxOpen;
    Serial.println("Change MaxOpen");
    Serial.println(maxOpen);
     for(int i=0;i<20;i++){
     inData[i]=0;
   }
   index=0;
   return 0;
  }
}

if (strcmp(funcToken,"cl")==0)
{
 
    Serial.println("Close");
     autom=0;
     for(int i=0;i<20;i++){
     inData[i]=0;
   }
   index=0;
   return 1;
  }

if (strcmp(funcToken,"op")==0)
{
 
    Serial.println("Open");
     autom=0;
     for(int i=0;i<20;i++){
     inData[i]=0;
   }
   index=0;
   return 2;
  }

if (strcmp(funcToken,"au")==0)
{
 
    Serial.println("Auto");
     autom=1;
     for(int i=0;i<20;i++){
     inData[i]=0;
   }
   index=0;
   return 0;
  }

  for(int i=0;i<20;i++){
     inData[i]=0;
   }
   index=0;
return 0;
}

void sendStatus()
{
 // String status="Status: ";
  if (autom == 1)
  { 
      Serial.println("Status: stat automatisch");
  }
  else
  {
      Serial.println("Status: stat manuell");
  }
  delay(500);
  if ( auf == 1)
  {
     Serial.println("Status: auf offen");
  }
  else
  {
     Serial.println("Status: auf geschlosssen");
  }
  delay(500);
    Serial.println("Iterationen: ");
    Serial.println(maxInterations);
}

// the loop function runs over and over again forever
void loop() {
  sendStatus();
  ret= Comp ();

   if ( ret == 1 )
   {
     schliessen(); 
     
   }

   if ( ret == 2 )
   {
      oeffnen();
   }
   
  if (autom == 1)
  { 
    liesLichtsensor();

    if (wert4 < 1 && auf == 1 )
   {
     //Workaround, da sonst zu früh geschlossen wird
     delay(600000);
      schliessen();
    }
   if (wert4 > 70 && auf == 0 )
    {
     oeffnen();
   }
  }
  delay(5000);

}

/* if ( sens1Changed == 1 )
  {
  Serial.println("Sensor1 changed");
  }
  if ( sens2Changed == 1 )
  {
  Serial.println("Sensor2 changed");
  }*/

