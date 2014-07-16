#include <iostream>

#include "otlclient.h"

using namespace std;

int main(int argc, char *argv[])
{
    otlclient otl;
    if(argc < 3){
      cerr << "Usage: " << argv[0] << " <ip> <port>" << endl;
      return -1;
    }    
    ///////////////////////////////////////////////////////////////
    //			Conecta ao dispositivo 			//
    ////////////////////////////////////////////////////////////// 
    
    cout << "Conectando..." << endl;
    cout << otl.connect(argv[1], atoi(argv[2])) << endl;
    cout << "Timestamp: " << otl.getLastTimestamp() << endl;
    cout << "Message: " << otl.getLastMsg() << endl;
    cout << "Info: " << otl.getLastInfo() << endl;
   
    ///////////////////////////////////////////////////////////////
    //		Recupera a lista de sensores diponiveis		//
    //////////////////////////////////////////////////////////////
    
    cout << "\nGet SensorList: " << otl.updateSensorList() << endl;
    cout << "Timestamp: " << otl.getLastTimestamp() << endl;
    cout << "Message: " << otl.getLastMsg() << endl;
    cout << "Info: " << otl.getLastInfo() << endl;

    ///////////////////////////////////////////////////////////////
    //			Desliga todos sensores			//
    //////////////////////////////////////////////////////////////
    string sensor = "NetworkLocation";
    cout << "\nTurn " << sensor << " off: " << otl.turnOff(sensor, 0) << endl;
    cout << "Timestamp: " << otl.getLastTimestamp() << endl;
    cout << "Message: " << otl.getLastMsg() << endl;
    cout << "Info: " << otl.getLastInfo() << endl;
    
    sensor = "GPS";
    cout << "\nTurn " << sensor << " off: " << otl.turnOff(sensor, 0) << endl;
    cout << "Timestamp: " << otl.getLastTimestamp() << endl;
    cout << "Message: " << otl.getLastMsg() << endl;
    cout << "Info: " << otl.getLastInfo() << endl;
    
    sensor = "CameraS";
    cout << "\nTurn " << sensor << " off: " << otl.turnOff(sensor, 0) << endl;
    cout << "Timestamp: " << otl.getLastTimestamp() << endl;
    cout << "Message: " << otl.getLastMsg() << endl;
    cout << "Info: " << otl.getLastInfo() << endl;
    
    sensor = "Accelerometer";
    cout << "\nTurn " << sensor << " off: " << otl.turnOff(sensor, 0) << endl;
    cout << "Timestamp: " << otl.getLastTimestamp() << endl;
    cout << "Message: " << otl.getLastMsg() << endl;
    cout << "Info: " << otl.getLastInfo() << endl;
    
    ///////////////////////////////////////////////////////////////
    //			Encerra a conexao			//
    //////////////////////////////////////////////////////////////    
    
    cout << "\nBye " << otl.close() << endl;
    cout << "Timestamp: " << otl.getLastTimestamp() << endl;
    cout << "Message: " << otl.getLastMsg() << endl;
    cout << "Info: " << otl.getLastInfo() << endl;
    
    return 0;
}
