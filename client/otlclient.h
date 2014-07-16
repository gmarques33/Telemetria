#ifndef OTLCLIENT_H
#define OTLCLIENT_H

#include <SDL_net.h>

#include <string>
#include <ctime>
#include <cstdlib>
#include <vector>

class otlclient
{
public:
    struct sensor
    {
        std::string type;
        int number;
    };
    otlclient();
    otlclient(std::string, int);
    virtual ~otlclient();
    std::string connect(std::string, int); //host, port
    std::string sendMessage(std::string); //message
    std::string receiveMessage(int);
    std::string getVersion();
    std::string getLastTimestamp();
    std::string getLastMsg();
    std::string getLastInfo();
    std::vector< otlclient::sensor > getSensorList();
    std::string updateSensorList();
    std::string get(std::string , int); //Sensor, numero
    std::string getBytes(std::string, int);
    char* receiveBytes(int);
    std::string turnOn(std::string , int); //Sensor, numero
    std::string turnOff(std::string , int); //Sensor, numero
    std::string close();

private:
    IPaddress ip;
    TCPsocket sd;
    std::string lastTimestamp;
    std::string lastMsg;
    std::string lastInfo;
    std::vector<sensor> sensorList;
};

#endif // OTLCLIENT_H
