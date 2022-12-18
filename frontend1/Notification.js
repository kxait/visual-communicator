import { $$ } from "./elemMake.js";

const Notification = ({ text, onclick = () => {} }) => 
    $$("div", { className: "notification", onclick }, [
        $$("img", { src: "icons/accept.png", alt: "success icon" }),
        $$("span", { innerText: text })
    ]);

export default Notification;