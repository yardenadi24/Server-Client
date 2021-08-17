
#include <iostream>
#include <string>
#include <vector>
#include <iterator>
#include <sstream>
#ifndef ASSIGN3_ENCODERDECODER_H
#define ASSIGN3_ENCODERDECODER_H
using  namespace std;

class EncoderDecoder {
public:
     EncoderDecoder();
    ~EncoderDecoder();
     void clear();
     string Encode(string Msg);
     short getType(string MessageType);
     short bytesToShort(int i);
     void shortToBytes(short num, vector<char> *bytesArr);
     string decodeNextByte(char nextByte);
     void popString();
private:
    int len;
    int pos;
    short messageOp;
    short typeOp;
    string decoMess;
    vector<char> byteVec;
    vector<string> stringVec;


};


#endif //ASSIGN3_ENCODERDECODER_H
