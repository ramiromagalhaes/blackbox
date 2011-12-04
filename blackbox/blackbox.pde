#define X_AXIS 1
#define Y_AXIS 2
#define Z_AXIS 3

#define BLUETOOTH_BAUD 9600

#define BLINKER 13

/*
'samplingRatePerSecond' armazena a quantidade de vezes por segundo com que os
dados serao amostrados. Coloquei isso numa variavel caso resolvamos permitir
a mudanca dessa taxa a partir do dispositivo de captura.
*/
int samplingRatePerSecond = 2;

boolean isConnected = false;

/*
Espera a resposta do dispositivo responsavel pelo envio de dados sobre os
sensores. quando houver dados, retorna a quantidade disponivel.
*/
byte communicationWaitForData() {
  byte dataAvailable = 0;
  while((dataAvailable = Serial.available()) <= 0) {
    delay(100);
  }

  return dataAvailable;
}

/*
Inicia a comunicacao serial com o fluxo (stream) que recebera as informacoes
sobre os sensores.

Se um dispositivo bluetooth for usado para este proposito, sera necessario
tambem garantir que uma conexao foi feita, enviando os comandos adequados.

O dispositivo bluetooth deve ficar nas portas digitais 0 e 1.

Para conhecer os detalhes dos comandos de conexao, vide o manual do modem
bluetooth usado:
http://www.sparkfun.com/datasheets/Wireless/Bluetooth/rn-bluetooth-um.pdf
*/
void setupCommunication() {
  byte dataAvailable = 0;

  Serial.begin(BLUETOOTH_BAUD);

  isConnected = true;
}

void setupGps() {
}

void setupAccelerometer() {
}

void setupSpeedometer() {
}

void setupBlinker() {
  pinMode(BLINKER, OUTPUT);
}

//retorna a velocidade em m/s
int getVelocity() {
  return 0;
}

//retorna a aceleracao em m/(s*s)
int getAcceleration(int axis) {
  return 0;
}

String getGps() {
  return "";
}

/*
Envia a velocidade atual, em m/s. Tal informacao ocupa 2 bytes, sendo
o primeiro o mais alto e o segundo o mais baixo.
*/
void sendVelocity(int velocity) {
  Serial.println( highByte(velocity), DEC );
  Serial.println( lowByte(velocity), DEC );
}

/*
Envia a aceleracao em um certo eixo. Tal informacao sera enviada
com 3 bytes. Os dois primeiros se referem a aceleracao propriamente
dita, sendo que o primeiro byte e a parte mais alta desse valor, e o
segundo e a parte mais baixa. O terceiro byte faz referencia ao eixo
enviado, conforme definido por X_AXIS, Y_AXIS e Z_AXIS.
*/
void sendAcceleration(int acceleration, int axis) {
  Serial.println( highByte(acceleration), DEC );
  Serial.println( lowByte(acceleration), DEC );
  Serial.println( lowByte(axis), DEC );
}

/*
Envia as informacoes fornecidas pelo GPS.
*/
void sendGps(String gps) {
  Serial.print(gps);
}

/*
Apenas um sinal de fim de transmissao, para dizer que um conjunto de sinais foi enviado.
*/
void sendDone() {
  Serial.println();
  Serial.println();
  Serial.println();
}

void blink() {
  digitalWrite(BLINKER, !digitalRead(BLINKER));
}

void setup() {
  setupCommunication();
  setupGps();
  setupAccelerometer();
  setupSpeedometer();
  setupBlinker();
}

void loop() {
  if ( !isConnected ) {
    //TODO tenta reconectar
    return;
  }

  //primeiro, pegamos os dados...
  int velocity = getVelocity();

  int accel_x = getAcceleration(X_AXIS);
  int accel_y = getAcceleration(Y_AXIS);
  int accel_z = getAcceleration(Z_AXIS);

  String gps = getGps();

  //depois, enviamos os dados...
  sendVelocity(velocity);

  sendAcceleration(accel_x, X_AXIS);
  sendAcceleration(accel_y, Y_AXIS);
  sendAcceleration(accel_z, Z_AXIS);

  sendGps(gps);

  sendDone();

  //piscamos, pra dizer que estamos vivos...
  blink();

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

