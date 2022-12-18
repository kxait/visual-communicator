import ConversationsPanel from "./ConversationsPanel.js";
import { $$, regeneratable } from "./elemMake.js";
import Username from "./Username.js";
import Messages from "./Messages.js";
import { sendSocket } from "./socket.js";
import { createSendMessageToConversationMessage } from "./createMessages.js";
import NewConversation from "./NewConversation.js";
import Settings from "./Settings.js";

const MainPanelState = {
    welcomeScreen: 0,
    conversation: 1,
    newConversation: 2,
    settings: 3
};

const MainPanel = () => {
    let unsubscribe = () => {};
    let currentConversationId = null;

    const sendMessage = text => {
        sendSocket(createSendMessageToConversationMessage(currentConversationId, text))
    }

    const onCreateConversation = data => {
        console.log(data);

        messages.regenerate({mainPanelState: MainPanelState.welcomeScreen});
    }

    const messages = regeneratable(({ conversationId, mainPanelState = MainPanelState.settings }) => 
        mainPanelState == MainPanelState.newConversation
            ? NewConversation({ onCreated: onCreateConversation })
            : mainPanelState == MainPanelState.welcomeScreen || mainPanelState == MainPanelState.conversation
                ? Messages({ 
                    conversationId, 
                    onSubscribe: fun => unsubscribe = fun,
                    onSendMessage: sendMessage
                })
                : mainPanelState == MainPanelState.settings
                    ? Settings()
                    : null);

    const onConversationChange = c => { 
        unsubscribe(); 
        messages.regenerate({ mainPanelState: MainPanelState.conversation, conversationId: c.id });
        currentConversationId = c.id;
    };

    const onNewConversation = () => {
        messages.regenerate({ mainPanelState: MainPanelState.newConversation, newConversation: true });
    }

    const logout = () => {
        localStorage.removeItem('authToken');
        location.reload();
    }

    const onSettings = () => {
        messages.regenerate({ mainPanelState: MainPanelState.settings });
    }

    return $$("div", {id: "main-panel"}, [
        $$("div", {id: "main-panel-top"}, [
            $$("div", { id:"main-panel-top-elements-container" }, [
                $$("h3", {className: "entry", innerText: "Visual Communicator"}),
            ]),
            $$("div", { id:"main-panel-top-elements-container", className: "middle" }, [
                Username(),
                $$("div", {className: "entry button cool", onclick: onSettings}, [
                    $$("img", { className: "icon", alt: "settings icon", src: "icons/cog.png" }),
                    $$("span", { innerText: "ustawienia" })
                ]),
                $$("div", {className: "entry button cool", onclick: logout }, [
                    $$("img", { className: "icon", alt: "wyloguj icon", src: "icons/server_go.png" }),
                    $$("span", { innerText: "wyloguj" })
                ]),
            ])
        ]),
        $$("div", { id: "main-panel-left-right" }, [
            $$("div", {id: "main-panel-left", className: "main-panel-pane cool-border"}, [
                ConversationsPanel({ onConversationChange, onNewConversation })
            ]),
            $$("div", { style: "overflow-y: scroll", id: "main-panel-right", className: "main-panel-pane cool-border"}, [
                messages.elem
            ])
        ])
    ]); 
}

export default MainPanel;