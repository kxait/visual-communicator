import { createWhoAmIMessage } from "./createMessages.js";
import { $$, regeneratable } from "./elemMake.js";
import Icon from "./Icon.js";
import { sendSocket } from "./socket.js";
import { state } from "./state.js";

const Username = () => {
    const getIcon = isAdmin => isAdmin
        ? Icon({ path: "icons/shield.png", alt: "admin icon" })
        : Icon({ path: "icons/user_gray.png", alt: "user icon" });

    const usernamePanel = regeneratable(({ isAdmin = false }) =>
        $$("div", {className: 'entry'}, [
            getIcon(isAdmin),
            $$("span", { innerText: state().username })
        ])
    );

    sendSocket(createWhoAmIMessage())
        .then(data => {
            usernamePanel.regenerate({ username: state().username, isAdmin: data.isAdmin })
        })
        .catch(e => {
            console.error(e);
        })

    return usernamePanel.elem; 
}

export default Username;