import { createGetConversationsMessage, createGetUsernameOfUserIdMessage, createWhoAmIMessage } from "./createMessages.js";
import { sendSocket, subscribeSocket } from "./socket.js";
import { $$, regeneratable } from "./elemMake.js";
import { messageTypes } from "./messageTypes.js";


const ConversationsPanel = ({ onConversationChange, onNewConversation }) => {
    let conversationNameById = {};
    const countConversations = () => Object.keys(conversationNameById).length

    const getConversationIcon = c => c.recipients.length === 2 
        ? "icons/user.png"
        : "icons/group.png"

    const generateConversationNamesFromConversations = data => new Promise((res, rej) => {
        conversationNameById = {};
        sendSocket(createWhoAmIMessage())
            .then(whoAmIResponse => {
                const id = whoAmIResponse.userId
                for(const i of data.conversations) {
                    if(i.recipients.length === 2) {
                        const converseeUserId = i.recipients.find(r => r !== id);
                        sendSocket(createGetUsernameOfUserIdMessage(converseeUserId))
                            .then(converseeName => {
                                conversationNameById[i.id] = converseeName.username;
                                if(countConversations() >= data.conversations.length)
                                    res();
                            }).catch(rej);
                    }else{
                        conversationNameById[i.id] = i.name;
                    }
                }
            }).catch(rej);
    });

    const conversations = regeneratable(({ conversations = [] }) => {
        return $$("div", { 
            id:"main-panel-left-elements-container" 
        }, [
            $$("div", { 
                style: "width:fit-content; margin:5px; margin-bottom: 20px", 
                className: "entry button cool", 
                onclick: onNewConversation}, [
                    $$("img", { className: "icon", src: "icons/email_add.png", alt: "new conversation icon"}),
                    $$("span", { innerText: "nowa konwersacja" })
                ]),
            ...(conversations.map(c => 
                $$("div", { 
                    className: "entry button", 
                    onclick: () => onConversationChange(c) }, [
                        $$("img", { className: "icon", alt: "conversation button", src: getConversationIcon(c) }),
                        $$("span", { innerText: conversationNameById[c.id]} )
                    ])))
        ]);
    })

    const regenerateConversations = () => 
        sendSocket(createGetConversationsMessage()).then(data => {
            generateConversationNamesFromConversations(data)
                .then(() => 
                    conversations.regenerate({ conversations: data.conversations }));
        });
    regenerateConversations()

    subscribeSocket(data => {
        if(data.type !== messageTypes.serverNewConversation
            && data.type !== messageTypes.clientCreateNewConversation) return;
        regenerateConversations();
    })

    return conversations.elem;
}

export default ConversationsPanel;