import { $$ } from "./elemMake.js";

const Icon = (({ path, alt = "icon" }) => $$("img", { 
    src: path, 
    className: "icon", 
    alt
}));

export default Icon;