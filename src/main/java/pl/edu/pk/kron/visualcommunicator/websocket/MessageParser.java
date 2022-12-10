package pl.edu.pk.kron.visualcommunicator.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import pl.edu.pk.kron.visualcommunicator.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.model.messages.*;

import java.net.InetSocketAddress;
import java.util.UUID;

public class MessageParser {
    private final Gson gson;

    public MessageParser() {
        this.gson = new Gson();
    }

    public MessageFromClientCombined parseMessageFromClient(String data, UUID from, InetSocketAddress address) {
        MessageFromClient template;
        MessageType type;
        try {
            template = gson.fromJson(data, MessageFromClient.class);
            if(template == null)  return null;
            type = template.getType();
            if(type == null)  return null;
        }catch(JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }

        var metadata = from == null ? new MessageFromClientMetadata(address) : new MessageFromClientMetadata(from, address);
        var gsonMessage = parseMessageFromClientByType(type, data).withId(metadata.getId());
        if(gsonMessage == null) return null;
        return new MessageFromClientCombined(gsonMessage, metadata);
    }

    private MessageFromClient parseMessageFromClientByType(MessageType type, String data){
        var $class = switch(type){
            case CLIENT_SEND_MESSAGE -> SendMessageToConversation.class;
            case CLIENT_GET_MESSAGES -> GetMessages.class;
            case CLIENT_GET_AUTH -> GetAuth.class;
            case CLIENT_GET_CONVERSATIONS -> GetConversations.class;
            case CLIENT_CREATE_NEW_CONVERSATION -> CreateConversation.class;
            case SERVER_NEW_MESSAGE -> NewMessageInConversationResponse.class;
            //case SERVER_ERR -> ErrResponse.class;
            case SERVER_NEW_CONVERSATION -> NewConversationResponse.class;
            case SERVER_PING -> PingResponse.class;
            default -> throw new IllegalArgumentException(type.toString());
        };

        try {
            return gson.fromJson(data, $class);
        }catch(JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
