import { createWhoAmIMessage } from "./createMessages.js";
import { $$ } from "./elemMake.js";
import { sendSocket } from "./socket.js";

const Username = () => {
    const usernamePanelGenerator = text => $$("div", {className: 'entry', innerText: text});
    const usernamePanel = usernamePanelGenerator();
    const generateUsernamePanel = text => usernamePanel.replaceWith(usernamePanelGenerator(text));

    sendSocket(createWhoAmIMessage())
        .then(data => {
            generateUsernamePanel(data.userName)
        })
        .catch(e => {
            console.error(e);
        })

    generateUsernamePanel();
    return usernamePanel; 
}

export default Username;