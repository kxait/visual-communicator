import badWords from "./badWords.js";
import { $$ } from "./elemMake.js"

export const Message = ({ author = "", message = "" }) => {
    for(const badWord of badWords) {
        if(message.toLowerCase().includes(badWord.toLowerCase())) {
            const start = message.indexOf(badWord);
            const len = badWord.length;

            message = message.substring(0, start) + "*".repeat(len) + message.substring(start+len);
        }
    }

    return $$("div", {}, [
        $$("span", { style: 'font-weight: bold; margin-right: 5px', innerText: `${author}:`}),
        $$("span", {innerText: message})
    ]);
}