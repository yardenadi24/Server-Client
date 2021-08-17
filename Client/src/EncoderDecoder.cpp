#include <string>
#include <vector>
#include <boost/lexical_cast.hpp>
#include <string>
#include <boost/algorithm/string.hpp>
#include <sstream>
#include "../include/EncoderDecoder.h"

using  namespace std;

//--constructor--
EncoderDecoder::EncoderDecoder():len(0),pos(0),messageOp(-1),typeOp(-1),decoMess(""),byteVec(),stringVec(){}
//--destructor--//
EncoderDecoder::~EncoderDecoder(){
    clear();
}

void EncoderDecoder::clear(){
    byteVec.clear();
    stringVec.clear();
    len=0;
    pos=0;
    messageOp=-1;
    typeOp=-1;
}

string EncoderDecoder::Encode(std::string message) {
    string encodedMessage = "";
    vector<char> tempToAdd;
    short myShort;

    //get the first word of the message which describes the type of the message
    int first = message.find_first_of(" ");
    string MessageType = message.substr(0, first);
    //convert to short operation code
    short opCode = getType(MessageType);
    string s = message;
    stringstream s1(s);
    string segment;
    //gives us a vector of string each cell contains a word
    while(std::getline(s1,segment,' ')){
       stringVec.push_back(segment);
    }
    //for ech type of message encode differently
    switch (opCode) {
        case 1:
        case 2:
        case 3:
            shortToBytes(opCode, &byteVec);
            encodedMessage += byteVec[0];
            encodedMessage += byteVec[1];
            encodedMessage += stringVec[1];
            encodedMessage += '\0';
            encodedMessage += stringVec[2];
            encodedMessage += '\0';
            clear();
            return encodedMessage;
        case 4:
        case 11:
            shortToBytes(opCode, &byteVec);
            encodedMessage += byteVec[0];
            encodedMessage += byteVec[1];
            clear();
            return encodedMessage;
        case 5:
        case 6:
        case 7:
        case 9:
        case 10:
            shortToBytes(opCode, &byteVec);
            encodedMessage += byteVec[0];
            encodedMessage += byteVec[1];
            myShort = boost::lexical_cast<short>(stringVec[1]);
            tempToAdd.clear();
            shortToBytes(myShort,&tempToAdd);
            encodedMessage += tempToAdd[0];
            encodedMessage += tempToAdd[1];
            clear();
            return encodedMessage;
        case 8:
            shortToBytes(opCode, &byteVec);
            encodedMessage += byteVec[0];
            encodedMessage += byteVec[1];
            encodedMessage += stringVec[1];
            encodedMessage += '\0';
            clear();
            return encodedMessage;

    }
        return encodedMessage;
}

short EncoderDecoder::getType(string MessageType){
    if(MessageType=="ADMINREG"){
        return 1;}
    if(MessageType=="STUDENTREG"){
        return 2;}
    if(MessageType=="LOGIN"){
        return 3;}
    if(MessageType=="LOGOUT"){
        return 4;}
    if(MessageType=="COURSEREG"){
        return 5;}
    if(MessageType=="KDAMCHECK"){
        return 6;}
    if(MessageType=="COURSESTAT"){
        return 7;}
    if(MessageType=="STUDENTSTAT"){
        return 8;}
    if(MessageType=="ISREGISTERED"){
        return 9;}
    if(MessageType=="UNREGISTER"){
        return 10;}
    if(MessageType=="MYCOURSES"){
        return 11;}
    return 0;
}

short EncoderDecoder::bytesToShort(int i)
{
    short result = (short)((byteVec[i] & 0xff) << 8);
    result += (short)(byteVec[i+1] & 0xff);
    pos=pos+2;
    return result;
}

void EncoderDecoder::shortToBytes(short num, vector<char> *bytesArr)
{
    bytesArr->push_back((num >> 8) & 0xFF);
    bytesArr->push_back(num & 0xFF);
}

 string EncoderDecoder::decodeNextByte(char nextByte) {
    if(messageOp<0) {
        if (len == 1) {
            //get op code
            byteVec.push_back(nextByte);
            len++;
            messageOp = bytesToShort(0);
            return "";
        }
        byteVec.push_back(nextByte);
        len++;
        return "";
    }
    if(len==3){
        //get opType
        byteVec.push_back(nextByte);
        len++;
        typeOp = bytesToShort(2);
    }
    //if Error
    if (messageOp == 13 && len>3) {
        //we have opcode and type so just send mess
            string s = "";
            s ="ERROR|"+std::to_string(typeOp);
            clear();
            return s;
        }
    //if ACK
    if(messageOp == 12 && len>3){

        //normal ACK
        if(typeOp==1||typeOp==2||typeOp==3||typeOp==4||typeOp==5||typeOp==10){
            string s ="";
            s ="ACK|"+std::to_string(typeOp);
            clear();
            return s;
        }
        if(typeOp==6||typeOp==9||typeOp==11) {
            if (nextByte == '\0') {
                popString();
                string s1 = stringVec[0];
                string s="ACK|"+std::to_string(typeOp)+"|"+s1;
                clear();
                return s;
            }
            byteVec.push_back(nextByte);
            len++;
            return "";
        }

        if(typeOp==8){
            if(nextByte=='\0'){
                popString();
                if(stringVec.size()==2){
                    string s1 = stringVec[0];
                    string s2 = stringVec[1];
                    clear();
                    return "ACK|"+to_string(8)+"|"+s1+"|"+s2;
                }
                return "";
            }
            byteVec.push_back(nextByte);
            len++;
            return "";
        }
        if(typeOp==7){
            if(nextByte=='\0'){
                popString();
                if(stringVec.size()==3){
                    string s1 = stringVec[0];
                    string s2 = stringVec[1];
                    string s3 = stringVec[2];
                    clear();
                    return "ACK|"+std::to_string(7)+"|"+s1+"|"+s2+"|"+s3;
                }
                return "";
            }
            byteVec.push_back(nextByte);
            len++;
            return "";
        }

    }
     byteVec.push_back(nextByte);
     len++;
    return "";  //not message yet
}

void EncoderDecoder::popString(){
    //create new vec to iterate on from curr pos to len
    //extracting the string we want to add the out put string
    vector<char> *temp = new vector<char>;
    //copy the data from pos to len
    int index=pos;
    if(typeOp==9||typeOp==11||typeOp==6){index=pos+1;}
    if((typeOp==8||typeOp==7)&&stringVec.size()==0){index=pos+1;}
    for(int i=index;i<len;i++){
        temp->push_back(byteVec[i]);
    }
    //creating string from the data from pos to len
    string res(temp->begin(),temp->end());
    pos=len;
    if(res.size()>0){
        stringVec.push_back(res);
    }
}




