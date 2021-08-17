#include <boost/lexical_cast.hpp>
#include <thread>
#include <mutex>
#include "../include/EncoderDecoder.h"
#include "../include/ClientSend.h"
#include "../include/ClientGet.h"


int main(int argc, char *argv[]){
    if(argc<3){
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    //indicator to terminate
    bool shouldTerminate = false;
    //indicator to log in
    bool isLoggedIn= true;
    //host
    std::string host = argv[1];
    //port
    short port =atoi(argv[2]);
    //create connection handler for the client
    ConnectionHandler *connectionHandler= new ConnectionHandler(host, port);
    //connect client
    connectionHandler->connect();
    std::mutex mutex;
    ClientGet clientGet(connectionHandler,shouldTerminate,mutex,isLoggedIn);
    ClientSend clientSend(connectionHandler,shouldTerminate,mutex,isLoggedIn);
    std::thread th1(&ClientGet::run,&clientGet);
    std::thread th2(&ClientSend::run,&clientSend);
    th1.join();
    th2.join();
    connectionHandler->close();
    return 0;
}