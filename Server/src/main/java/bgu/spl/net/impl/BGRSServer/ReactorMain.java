package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.MessagingProtocolImp;
import bgu.spl.net.api.messages.Message;
import bgu.spl.net.impl.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Reactor;

public class ReactorMain {
    public static void main(String[] args) {
        Reactor server = new Reactor(Integer.decode(args[1]).intValue(),Integer.decode(args[0]).intValue(),()->new MessagingProtocolImp(),()->new MessageEncoderDecoderImpl<Message>());
        server.serve();
    }
}
