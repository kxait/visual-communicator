import { $$ } from "./elemMake.js";

const CoolButton = (({ style = "", onclick = () => {}, text = "Kliknij mnie", src = "icons/cog.png" }) => {
    return $$("div", { 
        style,
        className: "entry button cool", 
        onclick
    }, [
        $$("img", { className: "icon", src, alt: "button icon"}),
        $$("span", { innerText: text })
    ]);
})

export default CoolButton;