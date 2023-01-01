import { $$ } from "./elemMake.js";

const ClickableIcon = (({ path, alt = "icon", onclick = () => {}, title = null }) => {
    const props = { 
        src: path, 
        className: "icon", 
        alt,
        onclick,
        style: "cursor: pointer;"
    };
    return $$("img", title === null ? props : { ...props, title });
});

export default ClickableIcon;