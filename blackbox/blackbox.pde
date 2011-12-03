#define X_AXIS 1
#define Y_AXIS 2
#define Z_AXIS 3

#define BLUETOOTH_BAUD 9600

//Armazena a quantidade de vezes por segundo com que os dados serao amostrados
//Coloquei isso numa variavel caso resolvamos permitir a mudanca dessa taxa a partir do Android
int samplingRatePerSecond = 2;

void setupBluetooth() {
  Serial.begin(BLUETOOTH_BAUD);
}

void setupGps() {
}

void setupAccelerometer() {
}

void setupSpeedometer() {
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
  return 0;
}

void sendVelocity(int velocity) {
}

void sendAcceleration(int acceleration, int axis) {
}

void sendGps(String gps) {
}

void setup() {
  setupBluetooth();
  setupGps();
  setupAccelerometer();
  setupSpeedometer();
}

void loop() {
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

  //entao aguardamos meio segundo para repetir tudo de novo
  delay(1/samplingRatePerSecond);

  /*
  O que fazer se a conexao do bluetooth falhar? Tenho
  como controlar isso de modo a fazer o sistema voltar
  para um estado conectado?
  */
}

