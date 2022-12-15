import * as messages from './createMessages.js'
import { makeRoot } from './elemMake.js';
import Login from './Login.js';
import MainPanel from './MainPanel.js';
import { sendSocket, makeSocket, subscribeSocket } from './socket.js';

const isLoggedIn = () => {
    const x = localStorage.getItem("authToken");
    return x != null;
}

const notLoggedIn = () => {
    const logInPanel = Login({ onLogin: login });
    makeRoot([logInPanel]);
}

const loggedIn = () => {
    const token = localStorage.getItem("authToken");
    const packet = messages.createGetAuthMessage(token);
    sendSocket(packet).then(data => {
        const mainPanel = MainPanel();
        makeRoot([mainPanel]);
    }).catch(e => {
        console.error(e);
        console.error("login rejected!");
        localStorage.removeItem("authToken");
        const loginPanel = Login({ onLogin: login });
        makeRoot([loginPanel]);
    })
}

const login = async () => {
    const $ = data => document.querySelector(data);
    const username = $("#input-username").value;
    const password = $("#input-password").value;
    
    var packet = messages.createGetAuthTokenMessage(username, password);
    const result = await sendSocket(packet)
    handleLogin(result);
}

const handleLogin = result => {
    if(result == null) {
        console.error("login error!");
        return;
    }

    console.log("login success! " + JSON.stringify(result));

    localStorage.setItem("authToken", result.token.token);
    location.reload();
}

const main = async () => {
    await makeSocket('ws://localhost:8887');

    if(isLoggedIn()) {
        loggedIn();
    }else{
        notLoggedIn();
    }
}

export default {
    ...messages,
    main,
    login,
    sendSocket
};