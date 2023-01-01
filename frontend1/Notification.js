import { $$ } from "./elemMake.js";

const Notification = ({ text, onclick = () => {}, iconSrc }) => 
    $$("div", { className: "notification", onclick }, [
        $$("img", { src: iconSrc, alt: "success icon" }),
        $$("span", { innerText: text })
    ]);

export default Notification;