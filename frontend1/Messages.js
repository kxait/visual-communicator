import CoolButton from "./CoolButton.js";
import { createGetMessagesMessage, createGetUsernameOfUserIdMessage } from "./createMessages.js";
import { $$, asyncRegeneratable, regeneratable } from "./elemMake.js";
import { Message } from "./Message.js";
import { messageTypes } from "./messageTypes.js";
import { sendSocket, subscribeSocket } from "./socket.js";
import { state } from "./state.js";
import { userNameById } from "./userNameById.js";

const Messages = ({ conversationId, onSubscribe, onSendMessage }) => {
    if(conversationId == null) {
        return $$("div", { innerText: "wybierz konwersacje!" });
    }

    const messages = regeneratable(({ messages = [] }) => {
        const elem = $$("div", { className: "messages"})
        for(var i of messages) {
            let message = i.content;
            const mess = regeneratable(({ username = "" }) => Message({ author: username, message }));
            elem.appendChild(mess.elem);
            elem.scrollTo(0, elem.scrollHeight);
            userNameById(i.authorUserId)
                .then(username => {
                    mess.regenerate({ username })
                }).catch(e => console.error(e));
        }
        return elem;
    });

    const sendMessage = () => {
        const messageBox = document.querySelector("input#message-box");
        const text = messageBox.value
        messageBox.value = "";
        if(text.trim() == "") return;
        onSendMessage(text);
    }

    const conversationHeader = regeneratable(({ isAuthor }) => {
        const currentConversationName = state().conversationNameById[conversationId];

        const recipientsExcludingMyself = regeneratable(({ recipients = [" "] }) => {
            return recipients.length === 1 ? $$("div", { innerHTML: "&nbsp;" }) : $$("div", { innerText: recipients.join(", ") });
        });

        const recipientsExcludingMyselfPromises = state().conversations
            .filter(conversation => conversation.id === conversationId)[0]
            .recipients
            .filter(recipientId => recipientId !== state().userId)
            .map(async recipientId => await userNameById(recipientId));
        
        Promise.all(recipientsExcludingMyselfPromises).then(data => {
            recipientsExcludingMyself.regenerate({ recipients: data });
        })

        return $$("div", { id: "conversation-header" }, [
            $$("h3", { innerText: currentConversationName }),
            recipientsExcludingMyself.elem,
            $$("div", { style: "display: flex" }, [
                CoolButton({ text: "i'm a cool button", src: "icons/emoticon_evilgrin.png" })
            ])
        ]);
    });

    const div = $$("div", { style: "height:100% "}, [
        conversationHeader.elem,
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