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
const createGetAuthTokenMessage = (userName, passwordHash) => JSON.stringify({
    type: messageTypes.clientGetAuthToken,
    id: uuidv4(),
    userName,
    passwordHash,
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

export {
    createCreateConversationMessage,
    createGetAuthMessage,
    createGetAuthTokenMessage,
    createGetConversationsMessage,
    createGetMessagesMessage,
    createSendMessageToConversationMessage,
    createWhoAmIMessage,
    createGetUsernameOfUserIdMessage
}