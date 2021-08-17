#include <boost/lexical_cast.hpp>
#include <boost/algorithm/string.hpp>
#include <string>
#include <sstream>
#include "../include/ClientGet.h"

ClientGet::ClientGet(ConnectionHandler *connectionHandler, bool &shouldTerminate, std::mutex &mutex,
                     bool &isLoggedIn):connectionHandler(connectionHandler),shouldTerminate(shouldTerminate),mutex(mutex),isLoggedIn(isLoggedIn){}
int ClientGet::run() {
    //create encoderDecoder
    EncoderDecoder parse;

    //while clients is running try and get next message from him
    while(!shouldTerminate){
        //create a lock for the synchronize
        //create buffer for the encoding
        char buf[1024];
        //create string to store th replay from server
        std::string replay;
        if(!connectionHandler->getBytes(buf,1)){
            std::cout << "Disconnected. Exiting...(ClientGet)\n" << std::endl;
            break;
        }
        replay = parse.decodeNextByte(buf[0]);
        if(replay.size()>0){
            string s = replay;
            std::vector<string> output;
            stringstream s1(s);
            std::string segment;
            int counter =0;
            while(std::getline(s1,segment,'|')){
                output.push_back(segment);
            }

            //should have now vector of the output
             if(output[0]=="ERROR"){
                 std::cout<<output[0]+" "+output[1]<<std::endl;
                 replay = "";
             }
             if(output[0]=="ACK"){
                 if(output[1]=="1"|output[1]=="2"|output[1]=="3"|output[1]=="5"|output[1]=="10"){
                     std::cout<<output[0]+" "+output[1]<<std::endl;
                     replay = "";
                 }
                 else if(output[1]=="6"){
                     std::cout<<output[0]+" "+output[1]<<std::endl;
                     std::cout<<output[2]<<std::endl;
                     replay = "";
                 }
                 else if(output[1]=="7"){
                     std::cout<<output[0]+" "+output[1]<<std::endl;
                     std::cout<<"Course: "+output[2]<<std::endl;
                     std::cout<<"Seats Available: "+output[3]<<std::endl;
                     std::cout<<"Students Registered: "+output[4]<<std::endl;
                     replay = "";
                 }
                 else if(output[1]=="8"){
                     std::cout<<output[0]+" "+output[1]<<std::endl;
                     std::cout<<"Student: "+output[2]<<std::endl;
                     std::cout<<"Courses: "+output[3]<<std::endl;
                     replay = "";
                 }else if(output[1]=="9"){
                     std::cout<<output[2]<<std::endl;
                     replay = "";
                 }
                 else if(output[1]=="11"){
                     std::cout<<output[0]+" "+output[1]<<std::endl;
                     std::cout<<output[2]<<std::endl;
                     replay = "";

                 }else if(output[1]=="4"){
                     std::cout<<output[0]+" "+output[1]<<std::endl;
                     replay = "";
                     shouldTerminate=true;
                 }
             }
        }

    }
    return -1;
}