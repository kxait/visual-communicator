import ClickableIcon from "./ClickableIcon.js";
import { createGetMessagesMessage, createGetUsernameOfUserIdMessage } from "./createMessages.js";
import { $$, regeneratable } from "./elemMake.js";
import Icon from "./Icon.js";
import { MainPanelState, setMainPanelState } from "./mainPanelState.js";
import { Message } from "./Message.js";
import { messageTypes } from "./messageTypes.js";
import { profileData } from "./profileData.js";
import { sendSocket, subscribeSocket } from "./socket.js";
import { state } from "./state.js";
import { userNameById } from "./userNameById.js";

const Messages = ({ conversationId, onSubscribe }) => {
    if(conversationId == null) {
        return $$("div", { innerText: "wybierz konwersacje!" });
    }

    let blockedUsers = []

    const scroll = element => element.scrollTo(0, element.scrollHeight + 100000);

    const messages = regeneratable(({ messages = [] }) => {
        const elem = $$("div", { className: "messages"})
        for(const i of messages) {
            if(blockedUsers.includes(i.authorUserId))
                continue;

            let message = i.content;
            const mess = regeneratable(({ username = "" }) => Message({ author: username, message, millis: i.millis }));
            elem.appendChild(mess.elem);
            userNameById(i.authorUserId)
                .then(username => {
                    mess.regenerate({ username })
                    scroll(elem);
                }).catch(e => console.error(e));
        }
        const scroller = $$("div", { id: "scroller" });
        elem.append(scroller);
        return elem;
    });

    const sendMessage = () => {
        const messageBox = document.querySelector("input#message-box");
        const text = messageBox.value
        messageBox.value = "";
        if(text.trim() == "") return;
        sendSocket(createSendMessageToConversationMessage(conversationId, text))
    }

    const conversationHeader = regeneratable(() => {
        const currentConversationName = state().conversationNameById[conversationId];

        // const recipientsExcludingMyself = regeneratable(({ recipients = [" "] }) => {
        //     return recipients.length === 1 
        //         ? $$("div", { innerHTML: "&nbsp;" }) 
        //         : $$("div", { style: "display: flex; flex-flow: wrap" }, 
        //             recipients.map(recipient => 
        //                 $$("div", { style: "cursor: pointer; margin-right: 5px;", onclick: () => {} }, [
        //                     Icon({ path: "icons/user.png" }),
        //                     $$("span", { innerText: recipient.username })
        //                 ])));
        // });

        // const recipientsExcludingMyselfPromises = state().conversations
        //     .filter(conversation => conversation.id === conversationId)[0]
        //     .recipients
        //     .filter(recipientId => recipientId !== state().userId)
        //     .map(async recipientId => ({ id: recipientId, username: await userNameById(recipientId) }));
        
        // Promise.all(recipientsExcludingMyselfPromises).then(data => {
        //     recipientsExcludingMyself.regenerate({ recipients: data });
        // })

        const currentConversation = state().conversations
            .filter(conversation => conversation.id === conversationId)[0];

        return $$("div", { id: "conversation-header", style: " border-bottom: 1px dotted white; padding-bottom: 5px;" }, [
            $$("div", { 
                style: `
                    display: flex; 
                    align-items: center; 
                    margin-bottom: 5px;
                    justify-content: space-between;
                    width: 100%;
                `, 
                onclick: () => {}
            }, [
                $$("div", { style: "display: flex; align-items: center; width: 100%;" }, [
                    Icon({ path: "icons/comments.png" }),
                    $$("h3", { innerText: currentConversationName }),
                ])
            ]),
            ClickableIcon({ path: "icons/cog.png", onclick: () => {
                const shouldBeUserProfileLink = currentConversation.recipients.length === 2;
                const targetMainPanelState = shouldBeUserProfileLink 
                    ? MainPanelState.userProfile
                    : MainPanelState.conversationProfile;
                const additionalData = shouldBeUserProfileLink
                    ? { userId: (() => {
                        const otherRecipient = currentConversation.recipients
                            .filter(recipientId => recipientId !== state().userId )[0];
                        return otherRecipient;
                    })() }
                    : { conversationId: conversationId };

                setMainPanelState({ mainPanelState: targetMainPanelState, additionalData })
            } }),
            Icon({ path: "icons/group.png" }),
            $$("span", { innerText: `${currentConversation.recipients.length} członków`})
        ]);
    });

    const div = $$("div", { style: "height:100%;  "}, [
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
        scroll(messages.elem);
    }

    const unsubscribe = subscribeSocket(data => {
        if(data.type !== messageTypes.serverNewMessage)
            return;
        if(data.message.conversationId === conversationId)
            appendMessage(data);
    })
    onSubscribe(unsubscribe);

    (async () => {
        blockedUsers = (await profileData()).blockedUsers ?? [];
        messagesList = (await sendSocket(createGetMessagesMessage(conversationId))).messages;
        messages.regenerate({ messages: messagesList });
        scroll(messages.elem);
    })();

    return div;
}

export default Messages;