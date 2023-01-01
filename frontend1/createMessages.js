import { messageTypes } from './messageTypes.js'

function uuidv4() {
    return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
        (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    )
}

// recipients: list<uuid>
const createCreateConversationMessage = (recipients, name) => JSON.stringify({
    type: messageTypes.clientCreateNewConversation,
    id: uuidv4(),
    recipients,
    name
});

// token: str
const createGetAuthMessage = token => JSON.stringify({
    type: messageTypes.clientGetAuth,
    id: uuidv4(),
    token
});

// userName, passwordHash: string
const createGetAuthTokenMessage = (userName, passwordHash, salt) => JSON.stringify({
    type: messageTypes.clientGetAuthToken,
    id: uuidv4(),
    userName,
    passwordHash,
    salt
});

const createGetConversationsMessage = conversationId => JSON.stringify({
    type: messageTypes.clientGetConversations,
    id: uuidv4(),
    conversationId
});

// conversationId: uuid
const createGetMessagesMessage = conversationId => JSON.stringify({
    type: messageTypes.clientGetMessages,
    id: uuidv4(),
    conversationId
});

// conversationId: uuid
// content: str
const createSendMessageToConversationMessage = (conversationId, content) => JSON.stringify({
    type: messageTypes.clientSendMessage,
    id: uuidv4(),
    conversationId,
    content
});

const createWhoAmIMessage = () => JSON.stringify({
    type: messageTypes.clientWhoAmI,
    id: uuidv4(),
});

const createGetUsernameOfUserIdMessage = userId => JSON.stringify({
    type: messageTypes.clientGetUsernameOfUserId,
    id: uuidv4(),
    userId
});

const createGetAvailableMessageRecipientsMessage = () => JSON.stringify({
    type: messageTypes.clientGetAvailableMessageRecipients,
    id: uuidv4()
});

const createGetUsersByIdOrPartOfName = input => JSON.stringify({
    type: messageTypes.clientGetUsersByIdOrPartOfName,
    id: uuidv4(),
    input
});

const createAdminCreateNewUser = (username, password, isAdmin) => JSON.stringify({
    type: messageTypes.clientAdminCreateNewUser,
    id: uuidv4(),
    username,
    password,
    isAdmin
});

const createAdminGetAllUsers = () => JSON.stringify({
    type: messageTypes.clientAdminGetAllUsers,
    id: uuidv4()
});

const createClientRenameMe = newName => JSON.stringify({
    type: messageTypes.clientRenameMe,
    id: uuidv4(),
    newName
});

const createClientChangeMyPassword = (currentPasswordHash, salt, newPasswordHash) => JSON.stringify({
    type: messageTypes.clientChangeMyPassword,
    id: uuidv4(),
    currentPasswordHash,
    salt,
    newPasswordHash
});

const createAdminRenameUser = (userToRename, newName) => JSON.stringify({
    type: messageTypes.clientAdminRenameUser,
    id: uuidv4(),
    userToRename,
    newName
});

const createAdminChangeUserActivated = (userId, activated) => JSON.stringify({
    type: messageTypes.clientAdminChangeUserActivated,
    id: uuidv4(),
    userId,
    activated
});

const createAdminChangeUserPassword = (userId, newPasswordHash) => JSON.stringify({
    type: messageTypes.clientAdminChangeUsersPassword,
    id: uuidv4(),
    userId,
    newPasswordHash
});

export {
    createCreateConversationMessage,
    createGetAuthMessage,
    createGetAuthTokenMessage,
    createGetConversationsMessage,
    createGetMessagesMessage,
    createSendMessageToConversationMessage,
    createWhoAmIMessage,
    createGetUsernameOfUserIdMessage,
    createGetAvailableMessageRecipientsMessage,
    createGetUsersByIdOrPartOfName,
    createAdminCreateNewUser,
    createAdminGetAllUsers,
    createClientRenameMe,
    createClientChangeMyPassword,
    createAdminRenameUser,
    createAdminChangeUserActivated,
    createAdminChangeUserPassword
}