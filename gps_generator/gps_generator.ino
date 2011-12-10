#include <string.h>
#include <ctype.h>

#define BLINKER 13 //pino da luz que pisca pra dizer que esta tudo operacional
#define SERIAL_BAUD 4800 // taxa de transmissao serial
#define GAP_HORA 10 // gap de hora para gerar o time stamp

void setup() {
  digitalWrite(BLINKER, HIGH);    
  Serial.begin(SERIAL_BAUD);	  
}

void loop() {
  Serial.println(geraGPRMC());    
  delay(500);
}

// Simula a string do comando GPRMC do protocolo NMEA:
// $GPRMC,220516,A,5133.82,N,00042.24,W,173.8,231.8,130694,004.2,W*70
String geraGPRMC() {
  String GPRMC = "$GPRMC,";
  
  // 1   Time Stamp
  GPRMC += getTimeStamp() + ",";     
  
  // 2   validity - A-ok, V-invalid
  GPRMC += "A,";
  
  // Coordenadas utilizadas
  // Origem: CCMN = "coordinates": [ -43.2389230, -22.8437590, 0 ]
  // Destino: CENTRO-ASSEMBLEIA 100 = coordinates": [ -43.1782404, -22.9061911, 0 ]
  unsigned long deltaLatitude = 229062 - 228438; // somar
  unsigned long deltaLongitude = 432389 - 431782; // subtrair
  unsigned long segundo = millis() / 1000;
  
  // 3   current Latitude
  unsigned long origemLatitude = 228438;
  GPRMC += getCoord(origemLatitude, deltaLatitude, segundo, true) + ",";   
  
  // 4   North/South
  GPRMC += "S,";
  
  // 5   Current Longitude
  unsigned long origemLongitude = 432389;
  GPRMC += getCoord(origemLongitude, deltaLongitude, segundo, false) + ",";     
  
  // 6   East/West
  GPRMC += "E,";
  
  // 7   Speed in knots  
  if ((segundo <= deltaLatitude) || (segundo <= deltaLongitude))
    GPRMC += "000.5,";      
  else
    GPRMC += "000.0,";      
  
  // 8   True course
  GPRMC += "360.0,";
  
  // 9   Date Stamp
  GPRMC += "141211,";
  
  // 10  Variation
  GPRMC += "011.3,";
  
  // 11  East/West
  GPRMC += "E";
  
  // 12  checksum
  GPRMC += "*62";
  
  return GPRMC;
}

// Gera o time stamp, primeiro valor do comando GPRMC
String getTimeStamp() {
  String timeStamp = "";
  unsigned long time = millis();
  unsigned long segundo = time / 1000;
  unsigned long minuto = segundo / 60;
  segundo -= 60 * minuto;
  unsigned long hora = minuto / 60;
  minuto -= 60 * hora;
  hora += GAP_HORA;
  unsigned long dia = hora / 24;
  hora -= 24 * dia;
  if (hora < 10)
    timeStamp += "0";
  timeStamp += hora;
  if (minuto < 10)
    timeStamp += "0";
  timeStamp += minuto;  
  if (segundo < 10)
    timeStamp += "0";
  timeStamp += segundo;    
  return timeStamp;
}

// Gera as coordenadas (latitude ou longitude) no formato do comando GPRMC
// Parametros:
// origem: Coordenada de origem
// delta: Delta entre origem e destino
// segundo: Numero de segundos desde que o programa comecou a ser executado
// somar: true - somar a parcela na origem; false - subtrair a parcela na origem
String getCoord(long origem, long delta, long segundo, boolean somar) {
  long coord = origem;
  long parcela = 0;
  if (segundo <= delta)
    parcela = segundo;
  else
    parcela = delta;
  if (somar)
    coord += parcela;
  else
    coord -= parcela;
  long coordd = coord / 100;
  long coorde = coord - coordd * 100;
  String retorno = String(coordd) + "." + String(coorde);

  return retorno;   

}  
