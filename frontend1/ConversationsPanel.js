import { createGetConversationsMessage } from "./createMessages.js";
import { sendSocket } from "./socket.js";
import { $$ } from "./elemMake.js";


const ConversationsPanel = ({ onConversationChange }) => {

    const conversationsPanel = $$("div", { id:"main-panel-left-elements-container" });

    sendSocket(createGetConversationsMessage()).then(data => {
        console.log(data);
        const conversationElems = data.conversations.map(c => 
            $$("div", { className: "entry button", onclick: () => onConversationChange(c), innerText: c.name }));
        conversationElems.forEach(ce => conversationsPanel.appendChild(ce));
    });

    return conversationsPanel;
}

export default ConversationsPanel;