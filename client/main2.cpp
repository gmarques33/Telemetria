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
    string sensor = "NetworkLocation";
    
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
    //			Liga um determinado sensor		//
    //////////////////////////////////////////////////////////////
    
    cout << "\nTurn " << sensor << " on: " << otl.turnOn(sensor, 0) << endl;
    cout << "Timestamp: " << otl.getLastTimestamp() << endl;
    cout << "Message: " << otl.getLastMsg() << endl;
    cout << "Info: " << otl.getLastInfo() << endl;

    ///////////////////////////////////////////////////////////////
    //		Le os dados um determinado sensor		//
    //////////////////////////////////////////////////////////////
    //while(1){
      cout << "\nGet " << sensor << ": " << otl.get(sensor, 0) << endl;
      cout << "Timestamp: " << otl.getLastTimestamp() << endl;
      cout << "Message: " << otl.getLastMsg() << endl;
      cout << "Info: " << otl.getLastInfo() << endl;
    //}

    ///////////////////////////////////////////////////////////////
    //			Desliga um determinado sensor		//
    //////////////////////////////////////////////////////////////

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
