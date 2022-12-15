import ConversationsPanel from "./ConversationsPanel.js";
import { $$ } from "./elemMake.js";
import Username from "./Username.js";
import Messages from "./Messages.js";
import { sendSocket } from "./socket.js";
import { createSendMessageToConversationMessage } from "./createMessages.js";

const MainPanel = () => {
    let unsubscribe = () => {};
    let currentConversationId = null;
    const generateMessagesElem = conversationId => Messages({ conversationId, onSubscribe: fun => unsubscribe = fun });
    const messagesElem = generateMessagesElem(null);
    const onConversationChange = c => { currentConversationId = c.id, unsubscribe(); messagesElem.replaceWith(generateMessagesElem(c.id)); };

    const sendMessage = () => {
        if(currentConversationId == null) return;
        const messageBox = document.querySelector("input#message-box");
        const text = messageBox.value
        sendSocket(createSendMessageToConversationMessage(currentConversationId, text)).then(d => {
            messageBox.value = "";
        })
    }

    return $$("div", {id: "main-panel"}, [
        $$("div", {id: "main-panel-top"}, [
            $$("div", { id:"main-panel-top-elements-container" }, [
                $$("h3", {className: "entry", innerText: "Visual Communicator"}),
            ]),
            $$("div", { id:"main-panel-top-elements-container", className: "middle" }, [
                Username(),
                $$("div", {className: "entry button", innerText: "settings"}),
                $$("div", {className: "entry button", innerText: "logout"}),
            ])
        ]),
        $$("div", { id: "main-panel-left-right" }, [
            $$("div", {id: "main-panel-left", className: "main-panel-pane"}, [
                ConversationsPanel({ onConversationChange })
            ]),
            $$("div", {id: "main-panel-right", className: "main-panel-pane"}, [
                messagesElem,
                $$("div", { className: "text-box"}, [
                    $$("form", { onsubmit: (e) => { sendMessage(); return false; } }, [
                        $$("input", { type:"text", id:"message-box" })
                    ])
                ])
            ])
        ])
    ]); 
}

export default MainPanel;