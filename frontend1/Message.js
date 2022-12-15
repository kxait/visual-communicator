import { $$ } from "./elemMake.js"

export const Message = ({ author, message }) => {
    return $$("div", {}, [
        $$("span", { style: 'font-weight: bold; margin-right: 5px', innerText: `${author}:`}),
        $$("span", {innerText: message})
    ]);
}