import ConversationsPanel from "./ConversationsPanel.js";
import { $$, regeneratable } from "./elemMake.js";
import Username from "./Username.js";
import Messages from "./Messages.js";
import { sendSocket } from "./socket.js";
import { createSendMessageToConversationMessage } from "./createMessages.js";
import NewConversation from "./NewConversation.js";

const MainPanel = () => {
    let unsubscribe = () => {};
    let currentConversationId = null;

    const sendMessage = text => {
        sendSocket(createSendMessageToConversationMessage(currentConversationId, text))
    }

    const onCreateConversation = data => {
        console.log(data);

        messages.regenerate({});
    }

    const messages = regeneratable(({ conversationId, newConversation = false }) => 
        newConversation
            ? NewConversation({ onCreated: onCreateConversation })
            : Messages({ 
                conversationId, 
                onSubscribe: fun => unsubscribe = fun,
                onSendMessage: sendMessage
            }));

    const onConversationChange = c => { 
        unsubscribe(); 
        messages.regenerate({ conversationId: c.id });
        currentConversationId = c.id;
    };

    const onNewConversation = () => {
        messages.regenerate({ newConversation: true });
    }

    const logout = () => {
        localStorage.removeItem('authToken');
        location.reload();
    }

    return $$("div", {id: "main-panel"}, [
        $$("div", {id: "main-panel-top"}, [
            $$("div", { id:"main-panel-top-elements-container" }, [
                $$("h3", {className: "entry", innerText: "Visual Communicator"}),
            ]),
            $$("div", { id:"main-panel-top-elements-container", className: "middle" }, [
                Username(),
                $$("div", {className: "entry button cool"}, [
                    $$("img", { className: "icon", alt: "settings icon", src: "icons/cog.png" }),
                    $$("span", { innerText: "ustawienia" })
                ]),
                $$("div", {className: "entry button cool", onclick: logout }, [
                    $$("img", { className: "icon", alt: "settings icon", src: "icons/server_go.png" }),
                    $$("span", { innerText: "ustawienia" })
                ]),
            ])
        ]),
        $$("div", { id: "main-panel-left-right" }, [
            $$("div", {id: "main-panel-left", className: "main-panel-pane cool-border"}, [
                ConversationsPanel({ onConversationChange, onNewConversation })
            ]),
            $$("div", {id: "main-panel-right", className: "main-panel-pane cool-border"}, [
                messages.elem
            ])
        ])
    ]); 
}

export default MainPanel;