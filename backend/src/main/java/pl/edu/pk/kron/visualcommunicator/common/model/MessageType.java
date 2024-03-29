package pl.edu.pk.kron.visualcommunicator.common.model;

public enum MessageType {
    CLIENT_GET_AUTH,
    CLIENT_GET_AUTH_TOKEN,
    CLIENT_GET_CONVERSATIONS,
    CLIENT_GET_MESSAGES,
    CLIENT_SEND_MESSAGE,
    CLIENT_CREATE_NEW_CONVERSATION,
    CLIENT_WHO_AM_I,
    CLIENT_GET_USERNAME_OF_USERID,
    CLIENT_GET_AVAILABLE_MESSAGE_RECIPIENTS,
    CLIENT_GET_USERS_BY_ID_OR_PART_OF_NAME,
    CLIENT_ADMIN_CREATE_NEW_USER,
    CLIENT_ADMIN_GET_ALL_USERS,
    CLIENT_RENAME_ME,
    CLIENT_CHANGE_MY_PASSWORD,
    CLIENT_ADMIN_RENAME_USER,
    CLIENT_ADMIN_CHANGE_USERS_PASSWORD,
    CLIENT_ADMIN_CHANGE_USER_ACTIVATED,
    CLIENT_GET_PROFILE_DATA,
    CLIENT_SET_PROFILE_DATA,
    CLIENT_ADMIN_GET_LOGS,


    SERVER_NEW_MESSAGE,
    SERVER_NEW_CONVERSATION,
    SERVER_ERR
}
