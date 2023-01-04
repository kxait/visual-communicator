import { createClientAdminGetLogs } from "./createMessages.js";
import { $$, regeneratable } from "./elemMake.js"
import { sendSocket } from "./socket.js";

const Logs = () => {

    const numberToGetChanged = data => {
        const newNum = Number.parseInt(data.srcElement.value);
        if(newNum === NaN || newNum === 0) return;
        
        get(newNum);
    }

    const log = regeneratable(({ logs = [] }) => {
        const messages = logs.map(log => {
            // log.message, log.date, log.severity
            return $$("tr", {}, [
                $$("td", { innerText: log.severity }),
                $$("td", { innerText: log.date }),
                $$("td", { innerText: log.message })
            ]);
        })

        return $$("div", {}, [
            $$("table", {}, [
                $$("tr", {}, [
                    $$("th", { innerText: "severity" }),
                    $$("th", { innerText: "time" }),
                    $$("th", { innerText: "message" })
                ]),
                ...messages
            ])
        ]);
    });

    const get = numberToGet => 
        sendSocket(createClientAdminGetLogs(numberToGet))
            .then(data => { log.regenerate({ logs: data.logs }); });

    get(100);

    return $$("div", { }, [ 
        $$("input", { oninput: numberToGetChanged, value: 100 }),
        log.elem 
    ]);
}

export default Logs;