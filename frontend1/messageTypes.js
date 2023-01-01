const messageTypes = {
    clientGetAuth: "CLIENT_GET_AUTH",
    clientGetAuthToken: "CLIENT_GET_AUTH_TOKEN",
    clientGetConversations: "CLIENT_GET_CONVERSATIONS",
    clientGetMessages: "CLIENT_GET_MESSAGES",
    clientSendMessage: "CLIENT_SEND_MESSAGE",
    clientWhoAmI: "CLIENT_WHO_AM_I",
    clientCreateNewConversation: "CLIENT_CREATE_NEW_CONVERSATION",
    clientGetUsernameOfUserId: "CLIENT_GET_USERNAME_OF_USERID",
    clientGetAvailableMessageRecipients: "CLIENT_GET_AVAILABLE_MESSAGE_RECIPIENTS",
    clientGetUsersByIdOrPartOfName: "CLIENT_GET_USERS_BY_ID_OR_PART_OF_NAME",
    clientAdminCreateNewUser: "CLIENT_ADMIN_CREATE_NEW_USER",
    clientAdminGetAllUsers: "CLIENT_ADMIN_GET_ALL_USERS",
    clientRenameMe: "CLIENT_RENAME_ME",
    clientChangeMyPassword: "CLIENT_CHANGE_MY_PASSWORD",
    clientAdminRenameUser: "CLIENT_ADMIN_RENAME_USER",
    clientAdminChangeUsersPassword: "CLIENT_ADMIN_CHANGE_USERS_PASSWORD",
    clientAdminChangeUserActivated: "CLIENT_ADMIN_CHANGE_USER_ACTIVATED",

    serverPing: "SERVER_PING",
    serverNewMessage: "SERVER_NEW_MESSAGE",
    serverNewConversation: "SERVER_NEW_CONVERSATION",
    serverErr: "SERVER_ERR"
};

export { messageTypes };