#include <boost/lexical_cast.hpp>
#include "../include/ClientSend.h"
ClientSend::ClientSend(ConnectionHandler *connectionHandler, bool &shouldTerminate, std::mutex &mutex,
                       bool &isLoggedIn):connectionHandler(connectionHandler),shouldTerminate(shouldTerminate), mutex(mutex),isLoggedIn(isLoggedIn) {}

int ClientSend::run() {
    EncoderDecoder parse;

    while(!shouldTerminate) {
        char buf[1024];
        if (isLoggedIn) {
            std::cin.getline(buf, 1024);
            std::string line(buf);
            string temp =line;
            line = parse.Encode(line);
            if(temp == "LOGOUT"){
                isLoggedIn=false;
            }
            if (line != "") {
                if (!connectionHandler->sendBytes(line.c_str(), line.size())) {
                    std::cout << "Disconnected. Exiting...(ClientSend)\n" << std::endl;
                    break;
                }
            }

        }
    }
    return -1;
}