import { $$ } from "./elemMake.js";

const Icon = (({ path, alt = "icon", title = null }) => {
    const props = { 
        src: path, 
        className: "icon", 
        alt
    };
    return $$("img", title === null ? props : { ...props, title });
});

export default Icon;