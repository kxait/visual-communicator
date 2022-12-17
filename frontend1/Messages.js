import { createGetMessagesMessage, createGetUsernameOfUserIdMessage } from "./createMessages.js";
import { $$, regeneratable } from "./elemMake.js";
import { Message } from "./Message.js";
import { messageTypes } from "./messageTypes.js";
import { sendSocket, subscribeSocket } from "./socket.js";

const Messages = ({ conversationId, onSubscribe, onSendMessage }) => {
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

    const messages = regeneratable(({ messages = [] }) => {
        const elem = $$("div", { className: "messages"})
        for(var i of messages) {
            let message = i.content;
            getUsernameById(i.authorUserId)
                .then(username => {
                    const mess = Message({ author: username, message })
                    elem.appendChild(mess);
                    elem.scrollTo(0, elem.scrollHeight);
                }).catch(e => console.error(e));
        }
        return elem;
    });

    const sendMessage = () => {
        if(conversationId == null) return;
        const messageBox = document.querySelector("input#message-box");
        const text = messageBox.value
        messageBox.value = "";
        if(text.trim() == "") return;
        onSendMessage(text);
    }

    const div = $$("div", { style: "height:100% "}, [
        messages.elem,
        $$("div", { className: "text-box" }, [
            $$("form", { onsubmit: (e) => { sendMessage(); return false; } }, [
                $$("input", { type:"text", id: "message-box" })
            ])
        ])
    ]);

        
    let messagesList = [];

    const appendMessage = message => { 
        messagesList.push(message.message); 
        messages.regenerate({ messages: messagesList });
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
            messages.regenerate({ messages: messagesList });
        }).catch(e => console.error(e));


    return div;
}

export default Messages;