#include "otlclient.h"
#define OTLVERSION 0001
#include <iostream>
#include <fstream>
#include <sys/time.h>
//Para proxima versao, deixar a variavel de erro global....
//trocar "sucess" por codigo de erro
//Para compilar: g++ -o out main.cpp otlclient.cpp `pkg-config sdl --libs --cflags` -lSDL_net

using namespace std;

otlclient::otlclient()
{
    
}

otlclient::otlclient(string host, int port)
{

    connect(host, port);

}

otlclient::~otlclient()
{

}

string otlclient::connect(string host, int port)
{

    char message[15];
    string error;

    //Init SDL
    if (SDLNet_Init() < 0)
    {
        return string(SDLNet_GetError());
    }

    // Resolve the host
    if (SDLNet_ResolveHost(&ip, host.c_str(), port) < 0)
    {
        return string(SDLNet_GetError());
    }

    // Open the connection
    if (!(sd = SDLNet_TCP_Open(&ip)))
    {
        return string(SDLNet_GetError());
    }

    //Send start msg
    sprintf(message, "hlo %.4d\n\r", OTLVERSION);
    error = sendMessage(string(message));
    if (error != "sucess")
    {
        return error;
    }

    //Verify if version are compatible
    error = receiveMessage(50);
    if (error != "sucess")
    {
        return error;
    }

    if (lastMsg != "hlo")
    {
        return "wrong message received";
    }
    if (lastInfo != getVersion())
    {
        return "wrong version";
    }

    return "sucess";
}

string otlclient::sendMessage(string message)
{
    int len;

    //Add timestamp to message
    char timestamp[20];
    sprintf(timestamp, "%d", (int) time(NULL));
    message = string(timestamp) + " " + message;

    //Send message
    len = strlen(message.c_str()) + 1;
    if (SDLNet_TCP_Send(sd, (void *)message.c_str(), len) < len)
    {
        return string(SDLNet_GetError());
    }

    return "sucess";
}

string otlclient::receiveMessage(int messageSize)
{
    char *buffer;
    int size, i;

    size = messageSize + 20; //20 plus chars to timestamp
    buffer = (char*) malloc(sizeof(char) * size);

    if (SDLNet_TCP_Recv(sd, buffer, size) > 0)
    {
        lastTimestamp = "";
        lastMsg = "";
        lastInfo = "";

        //Copy timestamp from received message
        for (i = 0; i <  size && buffer[i] != ' '; i++)
        {
            lastTimestamp += buffer[i];
        }
        i++;

        //Copy received message
        while (i < size && buffer[i] != ' ')
        {
            lastMsg += buffer[i];
            i++;
        }
        i++;

        //Copy received information
        while (i+1 < size && !(buffer[i] == '\n' && buffer[i+1] == '\r'))
        {
            lastInfo += buffer[i];
            i++;
        }
    }

    delete buffer;
    return "sucess";
}

char* otlclient::receiveBytes(int nBytes)
{
    char *buffer;
    int size, read = 0, current;

    size = nBytes;
    buffer = (char*) malloc(sizeof(char) * size);
    
   while(read < size){
    current = SDLNet_TCP_Recv(sd, &buffer[read], (size - read));
    read += current;
  }
   
  return buffer;
}
string otlclient::getVersion()
{

    char version[5];
    sprintf(version, "%.4d", OTLVERSION);
    return string(version);

}

string otlclient::getLastInfo()
{
    return lastInfo;
}

string otlclient::getLastMsg()
{
    return lastMsg;
}

string otlclient::getLastTimestamp()
{
    return lastTimestamp;
}

vector<otlclient::sensor> otlclient::getSensorList()
{
    if (sensorList.empty())
    {
        string error;
        error = updateSensorList();
        if (error != "sucess")
        {
            return sensorList;
        }
    }
    return sensorList;
}

string otlclient::updateSensorList()
{
    string error;
    string tmp = "";
    char message[10];
    unsigned int i, nSensors = 0;
    sensor sensorTemp;
    
    sprintf(message, "lst 0\n\r");
    error = sendMessage(string(message));
    if (error != "sucess")
    {
        return string(SDLNet_GetError());
    }

    error = receiveMessage(900);
    if (error != "sucess")
    {
        return string(SDLNet_GetError());
    }

    if (lastMsg != "lst")
    {
        return "wrong message received";
    }
    while (i < lastInfo.size() && lastInfo[i] != '\r') {
        //Get the number of sensors
        for (i = 0; (i < lastInfo.size() && lastInfo[i] != '\n'); i++)
        {
            tmp += lastInfo[i];
        }
        i++;
        nSensors = atoi(tmp.c_str());

        sensorTemp.type = "";
        tmp = "";
        for (;(i < lastInfo.size() || lastInfo[i] != ' '); i++)
        {
            sensorTemp.type += lastInfo[i];
        }
        i++;

        for (;(i < lastInfo.size() || lastInfo[i] != '\n'); i++)
        {
            tmp += lastInfo[i];
        }
        i++;
        sensorTemp.number = atoi(tmp.c_str());
        sensorList.push_back(sensorTemp);
    }
    return "sucess";
}

string otlclient::get(string sensor, int sensorNumber)
{
    char message[50];
    string error;

    sprintf(message, "get %s %.4d\n\r", sensor.c_str(), sensorNumber);
    
    error = sendMessage(string(message));
    if (error != "sucess")
    {
        return error;
    }

    error = receiveMessage(100);
    if (error != "sucess")
    {
        return error;
    }
    
    sprintf(message, "%s%.4d", sensor.c_str(), sensorNumber);
    if (lastMsg != message)
    {
        return "wrong message received";
    }
    
    return "sucess";
}

string otlclient::getBytes(string fileName, int nBytes){
  char message[50];
  char *bytes;
  string error;
  ofstream outfile;
  
    outfile.open(fileName.c_str());
    sprintf(message, "gtb 0\n\r");
    
    error = sendMessage(string(message));
    if (error != "sucess")
    {
        return error;
    }
    bytes = receiveBytes(nBytes);
    outfile.write(bytes, nBytes);
    outfile.close();
    return "sucess";
}

string otlclient::turnOn(string sensor, int sensorNumber){
    
    char message[50];
    string error;

    sprintf(message, "ton %s %.4d\n\r", sensor.c_str(), sensorNumber);
    
    error = sendMessage(string(message));
    if (error != "sucess")
    {
        return error;
    }

    error = receiveMessage(100);
    if (error != "sucess")
    {
        return error;
    }
    
    sprintf(message, "%s%.4d", sensor.c_str(), sensorNumber);
    if (lastInfo != message)
    {
        return "wrong message received";
    }
    
    return "sucess";
}

string otlclient::turnOff(string sensor, int sensorNumber){
  
    char message[50];
    string error;

    sprintf(message, "off %s %.4d\n\r", sensor.c_str(), sensorNumber);
    
    error = sendMessage(string(message));
    if (error != "sucess")
    {
        return error;
    }

    error = receiveMessage(100);
    if (error != "sucess")
    {
        return error;
    }
    
    sprintf(message, "%s%.4d", sensor.c_str(), sensorNumber);
    if (lastInfo != message)
    {
        return "wrong message received";
    }
    
    return "sucess";
  
}
string otlclient::close(){
    char message[10];
    string error;
    
    sprintf(message, "cls 0\n\r");
    
    error = sendMessage(string(message));
    if (error != "sucess")
    {
        return error;
    }

    error = receiveMessage(50);
    if (error != "sucess")
    {
        return error;
    }
    
    return "sucess";  
}
