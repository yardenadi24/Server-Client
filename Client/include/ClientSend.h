
#include "connectionHandler.h"
#include "EncoderDecoder.h"
#include <mutex>
#ifndef ASSIGN3_CLIENTSEND_H
#define ASSIGN3_CLIENTSEND_H


class ClientSend {
public:
    ClientSend(ConnectionHandler *connectionHandler, bool &shouldTerminate,std::mutex &mutex,bool &isLoggedIn);
    int run();
private:
    ConnectionHandler *connectionHandler;
    bool &shouldTerminate;
    std::mutex & mutex;
    bool &isLoggedIn;


};


#endif //ASSIGN3_CLIENTSEND_H
