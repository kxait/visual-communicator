const messageTypes = {
    clientGetAuth: "CLIENT_GET_AUTH",
    clientGetAuthToken: "CLIENT_GET_AUTH_TOKEN",
    clientGetConversations: "CLIENT_GET_CONVERSATIONS",
    clientGetMessages: "CLIENT_GET_MESSAGES",
    clientSendMessage: "CLIENT_SEND_MESSAGE",
    clientWhoAmI: "CLIENT_WHO_AM_I",
    clientCreateNewConversation: "CLIENT_CREATE_NEW_CONVERSATION",
    clientGetUsernameOfUserId: "CLIENT_GET_USERNAME_OF_USERID",
    serverPing: "SERVER_PING",
    serverNewMessage: "SERVER_NEW_MESSAGE",
    serverNewConversation: "SERVER_NEW_CONVERSATION",
    serverErr: "SERVER_ERR"
};

export { messageTypes };