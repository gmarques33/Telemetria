#include <iostream>
#include <cstdlib>
#include<opencv/cv.h>
#include<opencv/cxcore.h>
#include<opencv/highgui.h>

#include "otlclient.h"

using namespace std;

int main(int argc, char *argv[])
{
    otlclient otl;
    if(argc < 3){
      cerr << "Usage: " << argv[0] << " <ip> <port>" << endl;
      return -1;
    }
    string sensor = "CameraS";
    
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
    
      char fileName[50];
      IplImage* img = 0;
      IplImage* img2 = cvCreateImage( cvSize( 400, 300 ), IPL_DEPTH_8U, 3 );
      cvNamedWindow ("OTL", CV_WINDOW_AUTOSIZE);
      int x = 0;
    for(x = 0; x < 50; x++){
      cout << x << "/50" << endl;
      sprintf(fileName, "teste%.3d.jpg", x);
      cout << "\nGet " << sensor << ": " << otl.get(sensor, 0) << endl;
      cout << "Timestamp: " << otl.getLastTimestamp() << endl;
      cout << "Message: " << otl.getLastMsg() << endl;
      cout << "Info: " << otl.getLastInfo() << endl;
      if(atoi(otl.getLastInfo().c_str()) > 0)
	cout << "GetBytes: " << otl.getBytes(string(fileName), atoi(otl.getLastInfo().c_str())) << endl;
      img=cvLoadImage(fileName, CV_LOAD_IMAGE_UNCHANGED);
      cvResize(img, img2, 1);
      cvShowImage ("OTL", img2);
      cvWaitKey(0);
      sleep(1);
    }

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
