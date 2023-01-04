import { $$ } from "./elemMake.js";

const Error = ({ text, onclick = () => {} }) => 
    $$("div", { className: "error", onclick }, [
        $$("img", { src: "icons/cancel.png", alt: "error icon" }),
        $$("span", { innerText: text })
    ]);

export default Error;