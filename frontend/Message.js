import badWords from "./badWords.js";
import { $$ } from "./elemMake.js"

function generateLightColorRgb(name) {
    const rng = new Math.seedrandom(name);
    const red = Math.floor((1 + rng()) * 256/2);
    const green = Math.floor((1 + rng()) * 256/2);
    const blue = Math.floor((1 + rng()) * 256/2);
    return "rgb(" + red + ", " + green + ", " + blue + ")";
}

export const Message = ({ author = "", message = "" }) => {
    for(const badWord of badWords) {
        if(message.toLowerCase().includes(badWord.toLowerCase())) {
            const start = message.indexOf(badWord);
            const len = badWord.length;

            message = message.substring(0, start) + "*".repeat(len) + message.substring(start+len);
        }
    }

    const color = generateLightColorRgb(author);

    return $$("div", {}, [
        $$("span", { style: `color: ${color}; vertical-align: top; font-weight: bold; margin-right: 5px`, innerText: `${author}:`}),
        $$("span", { style: 'overflow: hidden; overflow-wrap: anywhere; display: inline-block;', innerText: message})
    ]);
}