import { createGetConversationsMessage, createGetUsernameOfUserIdMessage, createWhoAmIMessage } from "./createMessages.js";
import { sendSocket, subscribeSocket } from "./socket.js";
import { $$, regeneratable } from "./elemMake.js";
import { messageTypes } from "./messageTypes.js";
import { userNameById } from "./userNameById.js";
import { createNotification } from "./errorManager.js";
import { setState } from "./state.js";
import { setMainPanelState, MainPanelState } from "./mainPanelState.js";

const conversationsThatHaveNewMessagesLsKey = "conversationsThatHaveNewMessages";

const Conversation = ({ name, isMultipleRecipients, hasNewMessages, onConversationChange, isCurrentConversation }) => {
    const getConversationIcon = () => hasNewMessages
        ? "icons/bell.png"
        : isMultipleRecipients 
            ? "icons/group.png"
            : "icons/user.png";

    const spanStyle = `${hasNewMessages ? "font-weight: bold; " : ""}`;

    const containerStyle = `margin-bottom: 5px; ${isCurrentConversation ? "border-left: 3px solid white; padding-left: 3px" : ""}`;

    return $$("div", { 
        className: "entry button conversation-title", 
        style: containerStyle,
        onclick: () => onConversationChange() }, [
            $$("img", { 
                className: "icon", 
                alt: "conversation button", 
                src: getConversationIcon() }),
            $$("span", { innerText: name, style: spanStyle } )
    ]);
}

const ConversationsPanel = () => {

    const onConversationChange = c => { 
        setMainPanelState({ mainPanelState: MainPanelState.conversation, additionalData: { conversationId: c.id }})
    };

    const onNewConversation = () => {
        setMainPanelState({ mainPanelState: MainPanelState.newConversation, additionalData: { newConversation: true } })
    }

    let conversationsThatHaveNewMessages = JSON.parse(localStorage.getItem(conversationsThatHaveNewMessagesLsKey)) ?? [];

    let currentConversationId = null;
    const onConversationSelected = c => {
        currentConversationId = c.id;

        conversationsThatHaveNewMessages = conversationsThatHaveNewMessages
            .filter(conversationId => conversationId !== c.id);
        localStorage.setItem(conversationsThatHaveNewMessagesLsKey, JSON.stringify(conversationsThatHaveNewMessages));

        onConversationChange(c);
        regenerateConversations();
    }

    const onNewMessageSetUnreadConversation = conversationId => {
        if(conversationId === currentConversationId)
            return;
        conversationsThatHaveNewMessages.push(conversationId);
        localStorage.setItem(conversationsThatHaveNewMessagesLsKey, JSON.stringify(conversationsThatHaveNewMessages));
    }

    const onNewMessageCreateNotification = async (conversationId, authorId, content) => {
        if(conversationId === currentConversationId)
            return;

        const authorUsername = await userNameById(authorId);
        const conversationName = conversationNameById[conversationId];

        const conversationNameIfIsConversation = conversationName === authorUsername ? "" : `(${
            conversationName.length > 10 ? conversationName.substr(0, 10) + "..." : conversationName
        }) `;

        const notificationText = `${conversationNameIfIsConversation}${authorUsername}: ${content}`;
        
        createNotification({ text: notificationText, iconSrc: "icons/bell.png" });
    }

    let conversationNameById = {};

    const conversationHasNewMessages = conversationId => {
        return conversationsThatHaveNewMessages.includes(conversationId);
    }

    const generateConversationNamesFromConversations = async data => {
        conversationNameById = {};
        const whoAmIResponse = await sendSocket(createWhoAmIMessage());
        const id = whoAmIResponse.userId
        for(const i of data.conversations) {
            if(i.recipients.length === 2) {
                const converseeUserId = i.recipients.find(r => r !== id);
                const converseeName = await userNameById(converseeUserId);
                conversationNameById[i.id] = converseeName;
            }else{
                conversationNameById[i.id] = i.name;
            }
        }
        setState({ conversations: data.conversations, conversationNameById });
    };

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
            ...(conversations.map(c => Conversation({ 
                name: conversationNameById[c.id], 
                hasNewMessages: conversationHasNewMessages(c.id),
                isMultipleRecipients: c.recipients.length > 2,
                onConversationChange: () => onConversationSelected(c),
                isCurrentConversation: c.id === currentConversationId
            })))
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
            && data.type !== messageTypes.serverNewMessage) return;

        if(data.type === messageTypes.serverNewMessage) {
            onNewMessageSetUnreadConversation(data.message.conversationId);
            onNewMessageCreateNotification(data.message.conversationId, data.message.authorUserId, data.message.content);
        }

        regenerateConversations();

        if(data.type === messageTypes.serverNewConversation) {
            onNewMessageSetUnreadConversation(data.conversation.id)
            regenerateConversations();
        }
    })

    return conversations.elem;
}

export default ConversationsPanel;