import * as messages from "./createMessages.js";
import { $$, makeRoot } from "./elemMake.js";
import Login from "./Login.js";
import MainPanel from "./MainPanel.js";
import { MainPanelState, setMainPanelState } from "./mainPanelState.js";
import { sendSocket, makeSocket, subscribeSocket } from "./socket.js";
import { setState } from "./state.js";
import { makeid, sha256 } from "./utils.js";

const isLoggedIn = () => {
  const x = localStorage.getItem("authToken");
  return x != null;
};

const notLoggedIn = () => {
  const logInPanel = Login({ onLogin: login });
  makeRoot([logInPanel]);
};

const loggedIn = () => {
  const token = localStorage.getItem("authToken");
  const packet = messages.createGetAuthMessage(token);
  sendSocket(packet)
    .then((data) => {
      setState({ userId: data.userId, username: data.name });
      const mainPanel = MainPanel();
      makeRoot([mainPanel]);
    })
    .catch((e) => {
      console.error(e);
      console.error("login rejected!");
      localStorage.removeItem("authToken");
      const loginPanel = Login({ onLogin: login });
      makeRoot([loginPanel]);
    });
};

const connecting = () => {
  makeRoot([$$("h1", { innerText: "łączenie... " })]);
};

const couldNotConnect = () => {
  makeRoot([$$("h1", { innerText: "nie udało się połączyć :(" })]);
};

const login = async () => {
  const $ = (data) => document.querySelector(data);
  const username = $("#input-username").value;
  const password = $("#input-password").value;

  const sha256Password = await sha256(password);
  const salt = makeid(128);
  const sha256PasswordWithSalt = await sha256(sha256Password + salt);

  var packet = messages.createGetAuthTokenMessage(
    username,
    sha256PasswordWithSalt,
    salt
  );
  const result = await sendSocket(packet);
  handleLogin(result);
};

const handleLogin = (result) => {
  if (result == null) {
    console.error("login error!");
    return;
  }

  console.log("login success! " + JSON.stringify(result));

  localStorage.setItem("authToken", result.token.token);
  location.reload();
};

const main = async () => {
  connecting();
  try {
    await makeSocket(`ws://${window.location.hostname}:8887`);
  } catch (e) {
    couldNotConnect();
    return;
  }

  if (isLoggedIn()) {
    loggedIn();

    setTimeout(() => {
      setMainPanelState({
        "mainPanelState": 4,
        "additionalData": {
            "userId": "43befff0-513a-4962-98b7-e0f54866e628"
        }
    });
    }, 300);
  } else {
    notLoggedIn();
  }
};

export default {
  ...messages,
  main,
  login,
  sendSocket,
};
