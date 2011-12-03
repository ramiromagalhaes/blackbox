#define X_AXIS 1
#define Y_AXIS 2
#define Z_AXIS 3

void setupBluetooth() {
}

void setupBluetooth() {
}

void setup() {
  setupBluetooth();
  setupGps();
}

//retorna a velocidade em m/s
int getVelocity() {
  return 0;
}

//retorna a aceleraço em m/(s*s)
int getAcceleration(int axis) {
  return 0;
}

String getGps() {
  return null;
}

void sendVelocity(in velocity) {
}

void sendAcceleration(int acceleration, int axis) {
}

void sendGps(String gps) {
}

void loop() {
  int velocity = getVelocity();

  int accel_x = getAcceleration(X_AXIS);
  int accel_y = getAcceleration(Y_AXIS);
  int accel_z = getAcceleration(Z_AXIS);

  String gps = getGps();

  sendSpeed(velocity);

  sendAcceleration(accel_x, X_AXIS);
  sendAcceleration(accel_y, Y_AXIS);
  sendAcceleration(accel_z, Z_AXIS);

  sendGps(gps);

  delay(500); //aguarda meio segundo para enviar mais dados

  /*
  O que fazer se a conexao do bluetooth falhar? Tenho
  como controlar isso de modo a fazer o sistema voltar
  para um estado conectado?
  */
}

