import { $$, regeneratable } from "./elemMake.js";

function makeid(length) {
    var result           = '';
    var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var charactersLength = characters.length;
    for ( var i = 0; i < length; i++ ) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
}

async function sha256(message) {
    // encode as UTF-8
    const msgBuffer = new TextEncoder().encode(message);                    

    // hash the message
    const hashBuffer = await crypto.subtle.digest('SHA-256', msgBuffer);

    // convert ArrayBuffer to Array
    const hashArray = Array.from(new Uint8Array(hashBuffer));

    // convert bytes to hex string                  
    const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
    return hashHex;
}

function later(promise, fun = data => $$("span", { innerText: data })) {
    const obj = regeneratable(fun);

    promise.then(data => {
        obj.regenerate(data)
    })

    return obj;
}

function until(promise, funPlaceholder = data => $$("span", { innerText: "..." }), funLoaded = data => $$("span", { innerText: data })) {
    let loaded = false;
    const obj = regeneratable(data => loaded ? funLoaded(data) : funPlaceholder(data));

    promise.then(data => {
        loaded = true;
        obj.regenerate(data);
    });

    return obj;
}

export {
    later,
    until,
    makeid,
    sha256
}