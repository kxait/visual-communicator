import ConversationsPanel from "./ConversationsPanel.js";
import { $$, regeneratable } from "./elemMake.js";
import Username from "./Username.js";
import Messages from "./Messages.js";
import { sendSocket } from "./socket.js";
import { createSendMessageToConversationMessage } from "./createMessages.js";
import NewConversation from "./NewConversation.js";
import Settings from "./Settings.js";
import { MainPanelState, subscribeMainPanelState, setMainPanelState } from "./mainPanelState.js";
import { Message } from "./Message.js";

const MainPanel = () => {
    let unsubscribe = () => {};
    let currentConversationId = null;

    const sendMessage = text => {
        sendSocket(createSendMessageToConversationMessage(currentConversationId, text))
    }

    const onCreateConversation = data => {
        setMainPanelState({ mainPanelState: MainPanelState.welcomeScreen });
    }

    const mainPanel = regeneratable(({ mainPanelState = MainPanelState.welcomeScreen, additionalData = {} }) => {
        unsubscribe();
        
        if(mainPanelState === MainPanelState.welcomeScreen)
            return Messages({ 
                conversationId: null, 
                onSubscribe: fun => unsubscribe = fun
            });

        if(mainPanelState === MainPanelState.conversation)
            return Messages({
                conversationId: additionalData.conversationId,
                onSubscribe: fun => unsubscribe = fun,
                onSendMessage: sendMessage
            });

        if(mainPanelState === MainPanelState.newConversation)
            return NewConversation({ onCreated: onCreateConversation });

        if(mainPanelState === MainPanelState.settings)
            return Settings();

        if(mainPanelState === MainPanelState.userProfile)
            ;

        if(mainPanelState === MainPanelState.conversationProfile)
            ;
        
        return $$("div", { innerText: "oops" });
    });

    subscribeMainPanelState(({ mainPanelState, additionalData }) => mainPanel.regenerate({ mainPanelState, additionalData }));

    const onConversationChange = c => { 
        unsubscribe(); 
        setMainPanelState({ mainPanelState: MainPanelState.conversation, additionalData: { conversationId: c.id }})
        currentConversationId = c.id;
    };

    const onNewConversation = () => {
        setMainPanelState({ mainPanelState: MainPanelState.newConversation, additionalData: { newConversation: true } })
    }

    const logout = () => {
        localStorage.removeItem('authToken');
        location.reload();
    }

    const onSettings = () => {
        setMainPanelState({ mainPanelState: MainPanelState.settings });
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
                mainPanel.elem
            ])
        ])
    ]); 
}

export default MainPanel;