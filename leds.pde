/*
  Caixa preta.
*/

//Define em que portas devem ficar os leds
#define BLINK     2
#define NOT_BLINK 3
#define ALWAYS_ON 4
#define FADE      5

//Prepara para piscar os leds
void setup() {
  pinMode(BLINK, OUTPUT);
  pinMode(NOT_BLINK, OUTPUT);
  pinMode(ALWAYS_ON, OUTPUT);

  //sem pinMode para o FADE pois ele e analogico

  digitalWrite(ALWAYS_ON, HIGH);
}



boolean on = true; //diz se o piscador deve ou nao estar aceso
int intensity_increment = 5; //passo de incremento do led esmaecedor
int intensity = 0; //intensidade atual do esmaecedor

/*
Faz com que os piscadores pisquem...
Quando um estiver apagado, o outro deve estar aceso
*/
void doBlink() {
  if (on) {
    digitalWrite(BLINK, HIGH);
    digitalWrite(NOT_BLINK, LOW);
  } else {
    digitalWrite(BLINK, LOW);
    digitalWrite(NOT_BLINK, HIGH);
  }

  on = !on;
}

/* Apaga e acende gradualmente um led */
void doFade() {
  intensity += intensity_increment;
  if (intensity >= 255 || intensity <= 0) {
     intensity_increment = -intensity_increment;
  }

  analogWrite(FADE, intensity);
}

void loop() {
  doBlink();
  doFade();
  delay(150);
}

