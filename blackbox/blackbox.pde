#define X_AXIS 1 //identificador do eixo X do acelerometro
#define Y_AXIS 2 //identificador do eixo Y do acelerometro
#define Z_AXIS 3 //identificador do eixo Z do acelerometro

#define PIN_X 2 //pino de entrada do eixo X do acelerometro
#define PIN_Y 3 //pino de entrada do eixo Y do acelerometro

/*
Pino analogico usado pelo sensor de velocidade (distancia).
*/
#define PIN_VEL A0

#define BLINKER 13 //pino da luz que pisca pra dizer que esta tudo operacional

#define BLUETOOTH_BAUD 115200 //taxa de transmissao do dispositivo bluetooth.


/*
'samplingRatePerSecond' armazena a quantidade de vezes por segundo com que os
dados serao amostrados. Coloquei isso numa variavel caso resolvamos permitir
a mudanca dessa taxa a partir do dispositivo de captura.
*/
int samplingRatePerSecond = 2;


//flag para controlar como os dados serao exibidos: em modo de depuracao ou nao.
const boolean debug = true;

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

void setupGps() {
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

void setupDistanceSensor() {
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
Calcula a distancia em milimetros, conforme percebida pelo sensor de distancia.

NOTA IMPORTANTE: nao pudemos usar o sensor de distancia, portanto optamos por
simula-lo com um potenciometro. Isso nos obrigou a fazer algumas adaptacoes no
calculo da distancia, conforme descrito a seguir.

O sensor tem sensibilidade para detectar 256 unidades de distancia. Acontece
que o Arduino consegue absorver 1024 niveis diferentes de voltagem, portanto e
necessario dividir pela metade o resultado de analogRead(PIN_VEL) para termos a
distancia verdadeira. Essas unidades de distancia sao enviadas como polegadas.
Assim, e necessario converte-las.
*/
int getDistance() {
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

  return milliGravs/100; //converte milligravs em m/s*s
}

//retorna a aceleracao em m/(s*s)
int getAcceleration(int axis) {
  int data = 0;

  if (axis == X_AXIS) {
    data = pulseIn(PIN_X, HIGH);
  } else if (axis == Y_AXIS) {
    data = pulseIn(PIN_Y, HIGH);
  } //sem o pino da dimensao Z...

  return convertAcceleration(data);
}

String getGps() {
  return "";
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
Envia a aceleracao em um certo eixo. Tal informacao sera enviada
com 3 bytes. Os dois primeiros se referem a aceleracao propriamente
dita, sendo que o primeiro byte e a parte mais alta desse valor, e o
segundo e a parte mais baixa. O terceiro byte faz referencia ao eixo
enviado, conforme definido por X_AXIS, Y_AXIS e Z_AXIS.
*/
void sendAcceleration(int acceleration, int axis) {
  if (debug) {
   // Serial.print( axis, DEC );
   // Serial.print( ":" );
    Serial.println( acceleration, DEC );
  } else {
    Serial.write( highByte(acceleration) );
    Serial.write( lowByte(acceleration) );
    Serial.write( lowByte(axis) );
  }
}

/*
Envia as informacoes fornecidas pelo GPS.
*/
void sendGps(String gps) {
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
  setupDistanceSensor();
  setupBlinker();
  setupTimer();
}

void loop() {
  //primeiro, pegamos os dados...
  int velocity = getDistance();

  int accel_x = getAcceleration(X_AXIS);
  int accel_y = getAcceleration(Y_AXIS);
  int accel_z = getAcceleration(Z_AXIS);

  String gps = getGps();

  long timestamp = getTimestamp();

  //depois, enviamos os dados...
  sendDistance(velocity);

  sendAcceleration(accel_x, X_AXIS);
  sendAcceleration(accel_y, Y_AXIS);
  sendAcceleration(accel_z, Z_AXIS);

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

