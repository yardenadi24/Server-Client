package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.messages.Message;

import java.net.ServerSocket;
import java.util.function.Supplier;

public class ThreadsPerClientServer extends BaseServer<Message> {
    int port;
    Supplier<MessagingProtocol<Message>> protocolFactory;
    Supplier<MessageEncoderDecoder<Message>> encdecFactory;
    public ThreadsPerClientServer(  int port, Supplier<MessagingProtocol<Message>> protocolFactory, Supplier<MessageEncoderDecoder<Message>> encdecFactory){
        super(port,protocolFactory,encdecFactory);
    }

    @Override
    protected void execute(BlockingConnectionHandler<Message> handler) {
    new Thread(handler).start();
    }
}
