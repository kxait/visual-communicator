import { createGetMessagesMessage, createGetUsernameOfUserIdMessage } from "./createMessages.js";
import { $$ } from "./elemMake.js";
import { Message } from "./Message.js";
import { messageTypes } from "./messageTypes.js";
import { sendSocket, subscribeSocket } from "./socket.js";

const Messages = ({ conversationId, onSubscribe }) => {
    if(conversationId == null) {
        return $$("div", { innerText: "wybierz konwersacje!" });
    }

    let usernamesById = {};
    const getUsernameById = userId => new Promise((res, rej) => {
        if(usernamesById[userId] != null) {
            res(usernamesById[userId]);
            return;
        }
        
        sendSocket(createGetUsernameOfUserIdMessage(userId))
            .then(data => {
                usernamesById[userId] = data.username;
                res(data.username);
            }).catch(rej);        
    })

    // messages {id, authorUserId, content, conversationId}

    const generateMessagesElem = (messages = []) => {
        const elem = $$("div", { className: "messages" })
        for(var i of messages) {
            let message = i.content;
            getUsernameById(i.authorUserId)
                .then(username => {
                    const mess = Message({ author: username, message })
                    elem.appendChild(mess);
                }).catch(e => console.error(e));
        }
        return elem;
    }

    let msgs = generateMessagesElem();
    
    let messagesList = [];

    const appendMessage = message => { 
        messagesList.push(message.message); 
        const ele = generateMessagesElem(messagesList);
        msgs.replaceWith(ele);
        msgs = ele;

        msgs.scrollTo(0, msgs.scrollHeight);
    }

    const unsubscribe = subscribeSocket(data => {
        if(data.type !== messageTypes.serverNewMessage)
            return;
        if(data.message.conversationId === conversationId)
            appendMessage(data);
    })
    onSubscribe(unsubscribe);

    sendSocket(createGetMessagesMessage(conversationId))
        .then(data => {
            messagesList = data.messages;
            const x = generateMessagesElem(messagesList);
            msgs.replaceWith(x);
            msgs = x;

            msgs.scrollTo(0, msgs.scrollHeight);
        }).catch(e => console.error(e));


    return msgs;
}

export default Messages;