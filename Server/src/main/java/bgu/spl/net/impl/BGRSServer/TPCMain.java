package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessagingProtocolImp;
import bgu.spl.net.api.messages.Message;
import bgu.spl.net.impl.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.ThreadsPerClientServer;

public class TPCMain {
    public static void main(String[] args) {
        BaseServer server=new ThreadsPerClientServer(Integer.decode(args[0]).intValue(),()->new MessagingProtocolImp(),()->new MessageEncoderDecoderImpl<Message>());
        server.serve();
    }

    }

