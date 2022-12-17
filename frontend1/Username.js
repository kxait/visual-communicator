import { createWhoAmIMessage } from "./createMessages.js";
import { $$, regeneratable } from "./elemMake.js";
import { sendSocket } from "./socket.js";

const Username = () => {
    const usernamePanel = regeneratable(({ username = "" }) =>
        $$("div", {className: 'entry'}, [
            $$("img", { alt: "user icon", src: "icons/user_gray.png", className: "icon"}),
            $$("span", { innerText: username })
        ])
    );

    sendSocket(createWhoAmIMessage())
        .then(data => {
            usernamePanel.regenerate({ username: data.userName })
        })
        .catch(e => {
            console.error(e);
        })

    return usernamePanel.elem; 
}

export default Username;