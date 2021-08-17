

#include <bits/cpp_type_traits.h>
#include <string>
#include <vector>
#include <iterator>
#include <sstream>
#include <stdio.h>
#include <stdlib.h>
#include <mutex>
#include "EncoderDecoder.h"
#include <condition_variable>
#include "connectionHandler.h"
#ifndef ASSIGN3_CLIENTGET_H
#define ASSIGN3_CLIENTGET_H


using  namespace std;

class ClientGet {
public:
    ClientGet(ConnectionHandler *connectionHandler, bool &shouldTerminate,std::mutex& mutex,bool &isLoggedIn);
    int run();

private:
    ConnectionHandler *connectionHandler;
    bool &shouldTerminate;
    std::mutex & mutex;
    bool &isLoggedIn;
};


#endif //ASSIGN3_CLIENTGET_H
