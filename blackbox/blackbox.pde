#include <SoftwareSerial.h>
#include <math.h>

//identificadores dos eixos X, Y, Z do acelerometro
#define X_AXIS 1
#define Y_AXIS 2
#define Z_AXIS 3

//Pinos de entrada de dados dos eixos X e Y do acelerometro
#define PIN_X 2
#define PIN_Y 3

//Pino analogico usado pelo sensor de velocidade (distancia).
#define PIN_VEL A0

//Pinos destinados para o I/O do GPS.
#define PIN_GPS_IN 8
#define PIN_GPS_OUT 9

//pino da luz que pisca pra dizer que esta tudo operacional
#define BLINKER 13

//taxa de transmissao do dispositivo bluetooth.
#define BLUETOOTH_BAUD 115200

//taxa de transmissao do GPS
#define GPS_BAUD 4800

/*
'samplingRatePerSecond' armazena a quantidade de vezes por segundo com que os
dados serao amostrados. Coloquei isso numa variavel caso resolvamos permitir
a mudanca dessa taxa a partir do dispositivo de captura.
*/
int samplingRatePerSecond = 2;

//flag para controlar como os dados serao exibidos: em modo de depuracao ou nao.
const boolean debug = true;

/*
Altura em que o sonar fica do chao, em milimetros. E usada para calcular a
velocidade da bicicleta.
*/
int sonarHeight = 0;

/*
Valor da gravidade local, usado para calcular a inclinacao da bicicleta com o uso
do acelerometro.
*/
int gravity = 10;

//Controlador de I/O por software usado pelo (simulador do) GPS
SoftwareSerial gpsSerial(PIN_GPS_IN, PIN_GPS_OUT);

/*
Inicia a comunicacao serial com o fluxo (stream) que recebera as informacoes
sobre os sensores.

Se um dispositivo bluetooth for usado para este proposito, sera necessario
tambem garantir que uma conexao foi feita, enviando os comandos adequados.

O dispositivo bluetooth deve ficar nas portas digitais 0 e 1.

Para conhecer os detalhes dos comandos de conexao, vide o manual do modem
bluetooth usado:
http://www.sparkfun.com/datasheets/Wireless/Bluetooth/rn-bluetooth-um.pdf

O wiki tambem possui material sobre o assunto.
*/
void setupCommunication() {
  byte dataAvailable = 0;

  Serial.begin(BLUETOOTH_BAUD);
}

//Inicia a porta serial do GPS.
void setupGps() {
  gpsSerial.begin(GPS_BAUD);
}

/*
Inicia a comunicacao com o acelerometro. Este dispositivo se comunica
atraves das portas seriais PIN_X e PIN_Y. Essas 2 portas sao configuradas
como INPUT pois receberao dados digitais diretamente do acelerometro.
*/
void setupAccelerometer() {
  pinMode(PIN_X, INPUT);
  pinMode(PIN_Y, INPUT);
}

/*
Faz a leitura inicial da altura em que o sonar permanece no chao. Esta
distancia sera usada posteriormente para calculo de velocidade da bicicleta.

Essa distancia fica armazenada na  porta variavel 'sonarHeight'.
*/
void setupSonar() {
  sonarHeight = readDistance();
}

void setupBlinker() {
  pinMode(BLINKER, OUTPUT);
}

/*
Configuracao necessaria para pegarmos os dados do relogio do arduino.
*/
void setupTimer() {
}



/*
Obtem do sonar a distancia que ele mede. A saida esta em milimetros.

NOTA IMPORTANTE: nao pudemos usar o sensor de distancia, portanto optamos por
simula-lo com um potenciometro. Isso nos obrigou a fazer algumas adaptacoes no
calculo da distancia, conforme descrito a seguir.

O sensor tem sensibilidade para detectar 256 unidades de distancia. Acontece
que o Arduino consegue absorver 1024 niveis diferentes de voltagem, portanto e
necessario dividir pela metade o resultado de analogRead(PIN_VEL) para termos a
distancia verdadeira. Essas unidades de distancia sao enviadas como polegadas.
Assim, e necessario converte-las.
*/
int readDistance() {
  const float ajustes = 25.4/4.0;
  return analogRead(PIN_VEL) * ajustes;
}

/*
Converte o dado enviado pelo acelerometro em uma aceleracao em metros por segundo.
Como o retorno e do tipo inteiro, estamos ignorando a parte fracionaria da aceleracao.
*/
int convertAcceleration(int sensorData) {
  /*
  Para entender a equaçao a seguir, consulte:

  http://www.parallax.com/dl/docs/prod/acc/memsickit.pdf
  http://arduino.cc/en/Tutorial/Memsic2125?from=Tutorial.Accelerometer
  */
  const int milliGravs = ((sensorData / 10) - 500) * 8;

  return milliGravs/100; //converte milligravs em m/s²
}



/*
Retorna a velocidade atual da bicicleta em metros/segundo, conforme descrito na
seçao "Modulo Velocimetro" do wiki: https://github.com/ramiromagalhaes/blackbox/wiki.

Na equacao abaixo, currentDistance equivale a 'd', sonarHeigh equivale a 'di', e
totalTime equivale a 't'.
*/
int getSpeed() {
  const long startTime = millis();
  const int currentDistance = readDistance();
  const long totalTime = millis() - startTime; //TODO: falta subtrair os atrasos do hardware informados pelo fabricante

  return sqrt( pow(currentDistance, 2.0) - 4.0 * pow(sonarHeight, 2.0) ) / totalTime; //note as unidades: estamos fazendo
                                                                                      //milimetros/milissegundos, portanto
                                                                                      //temos o mesmo que metros/segundo
}

/*
Retorna a aceleracao da bicicleta em m/(s*s). Esta e a aceleracao da bicicleta
quando ela esta acelerando para frente ou para tras.
*/
int getAcceleration() {
  return convertAcceleration(pulseIn(PIN_X, HIGH));
}

/*
Retorna a inclinacao da bicicleta em graus em relacao ao chao. O angulo de inclinacao
sera positivo se a bicicleta inclinar para a esquerda e negativo se inclinar para a
direita. O valor zero se refere a bicicleta perfeitamente de pe.

O metodo de calculo da inclinacao esta descrito na seçao "Modulo Acelerometro" no wiki
em https://github.com/ramiromagalhaes/blackbox/wiki .
*/
int getInclination() {
  const int currentAcceleration = convertAcceleration(pulseIn(PIN_Y, HIGH));
  return acos(currentAcceleration/gravity) * 180.0 / M_PI;
}

/*
Retorna os dados do GPS.
*/
char* getGps() {
  char buffer[68] = {0};

  int charsAvalilable = gpsSerial.available();

  int i = 0;
  for (; i < charsAvalilable; i++) {
    char theChar = gpsSerial.read();
    //Serial.print(theChar);
    buffer[i] = theChar;
  }

  buffer[i+1] = 0;

  return buffer;
}

/*
Retorna o "timestamp" dos dados que foram coletados.

TODO: Vamos usar o tempo do arduino ou o tempo do Android?
*/
long getTimestamp() {
  return millis();
}

/*
Envia a distancia, em metros. Tal informacao ocupa 2 bytes, sendo
o primeiro o mais alto e o segundo o mais baixo.
*/
void sendDistance(int velocity) {
  if (debug) {
    Serial.println( velocity );
  } else {
    Serial.write( highByte(velocity) );
    Serial.write( lowByte(velocity) );
  }
}

/*
Envia a aceleracao linear da bicicleta. Tal informacao sera enviada
com 3 bytes. Os dois primeiros se referem a aceleracao propriamente
dita, sendo que o primeiro byte e a parte mais alta desse valor, e o
segundo e a parte mais baixa. O terceiro byte faz referencia ao eixo
enviado, conforme definido por X_AXIS, Y_AXIS e Z_AXIS.
*/
void sendAcceleration(int acceleration) {
  if (debug) {
    Serial.println( acceleration, DEC );
  } else {
    Serial.write( highByte(acceleration) );
    Serial.write( lowByte(acceleration) );
    Serial.write( lowByte(X_AXIS) );
  }
}


/*
Envia a inclinacao da bicicleta. Tal informacao sera enviada
com 3 bytes. Os dois primeiros se referem a aceleracao propriamente
dita, sendo que o primeiro byte e a parte mais alta desse valor, e o
segundo e a parte mais baixa. O terceiro byte faz referencia ao eixo
enviado, conforme definido por X_AXIS, Y_AXIS e Z_AXIS.
*/
void sendInclination(int angle) {
  if (debug) {
    Serial.println( angle, DEC );
  } else {
    Serial.write( highByte(angle) );
    Serial.write( lowByte(angle) );
    Serial.write( lowByte(Y_AXIS) );
  }
}

/*
Este metodo existe apenas para manter a compatibilidade com a aplicacao
Android.
*/
void sendNothing() {
  if (debug) {
    Serial.println( 0, DEC );
  } else {
    Serial.write( highByte(0) );
    Serial.write( lowByte(0) );
    Serial.write( lowByte(0) );
  }
}

/*
Envia as informacoes fornecidas pelo GPS.
*/
void sendGps(char* gps) {
  Serial.println(gps);
}

/*
Envia informacoes sobre o timestamp.
*/
void sendTimestamp(long timestamp) {
  if (debug) {
    Serial.print(timestamp);
  } else {
    Serial.write(timestamp & 0xFF000000);
    Serial.write(timestamp & 0x00FF0000);
    Serial.write(timestamp & 0x0000FF00);
    Serial.write(timestamp & 0x000000FF);
  }
}

/*
Apenas um sinal de fim de transmissao, para dizer que um conjunto de sinais foi enviado.
*/
void sendDone() {
  Serial.println();
  Serial.println();
  Serial.println();
}

/*
Se BLINKER estiver ligado, desliga, senao, liga.
*/
void doBlink() {
  digitalWrite(BLINKER, !digitalRead(BLINKER));
}

void setup() {
  setupCommunication();
  setupGps();
  setupAccelerometer();
  setupSonar();
  setupBlinker();
  setupTimer();
}

void loop() {
  //primeiro, pegamos os dados...
  int velocity = getSpeed();

  int accel = getAcceleration();
  int inclination = getInclination();

  char* gps = getGps();

  long timestamp = getTimestamp();

  //depois, enviamos os dados...
  sendDistance(velocity);

  sendAcceleration(accel);
  sendAcceleration(inclination);
  sendNothing(); //apenas para nao quebrar a compatibilidade com a aplicaçao do Android.

  sendGps(gps);

  sendTimestamp(timestamp);

  sendDone();

  //piscamos, pra dizer que estamos vivos...
  doBlink();

  //entao aguardamos meio segundo para repetir tudo de novo.
  delay( 1000/samplingRatePerSecond );

  /*
  O que fazer se a conexao do bluetooth cair? Tenho como controlar isso
  de modo a fazer o sistema voltar para um estado conectado?

  Em todo caso, interrupcoes sao enviadas atraves dos pinos digitais
  2 e 3 (interrupcoes 0 e 1, respectivamente). Talvez seja bom deixar
  esses dois pinos livres por padrao para evitar que algo de errado ocorra.
  */
}

