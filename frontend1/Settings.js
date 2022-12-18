import { createAdminCreateNewUser, createWhoAmIMessage } from "./createMessages.js";
import { $$, regeneratable } from "./elemMake.js"
import { sendSocket } from "./socket.js";
import Icon from "./Icon.js";
import { createNotification } from "./errorManager.js";

const Settings = () => {
    const userIdElem = regeneratable(({ userId = "" }) => 
        $$("span", { style: "font-family: monospace", innerText: userId }));

    const usernameElem = regeneratable(({ username = "" }) =>
        $$("input", { value: username, placeholder: "nazwa użytkownika" }));

    sendSocket(createWhoAmIMessage())
        .then(whoAmI => {
            userIdElem.regenerate({ userId: whoAmI.userId });
            usernameElem.regenerate({ username: whoAmI.userName });
            adminPanel.regenerate({ isAdmin: whoAmI.isAdmin });
        });

    const onChangeUsername = () => {
        const username = usernameElem.elem.value;
        console.log(username);
    }

    const onChangePassword = () => {
        const currentPassword = document.querySelector("#current-password").value;
        const newPassword = document.querySelector("#new-password").value;
        const newPasswordConfirm = document.querySelector("#new-password-confirm").value;

        console.log(currentPassword, newPassword, newPasswordConfirm)
    }

    const onAddUser = () => {
        const username = document.querySelector("#new-user-username").value;
        const password = document.querySelector("#new-user-password").value;

        sendSocket(createAdminCreateNewUser(username, password, false))
            .then(data => {
                createNotification("udalo sie stworzyc uzytkownika");
            })
    }

    const adminPanel = regeneratable(({ isAdmin = false }) => {
        return isAdmin
            ? $$("div", { }, [
                $$("div", {}, [
                    Icon({ path: "icons/accept.png" }),
                    $$("span", { innerText: "jestes administratorem!"})
                ]),
                $$("fieldset", {}, [
                    $$("legend", { innerText: "wszyscy użytkownicy"}),
                    $$("div", { innerText: "coś tu kiedyś będzie"})
                ]),
                $$("fieldset", {}, [
                    $$("legend", { innerText: "ustawienia administratora" }),
                    $$("div", {}, [
                        $$("div", { innerText: "stwórz użytkownika", style: "font-weight: bold" }),
                        $$("div", { className: "text-box" }, [
                            $$("input", { type: "text", id: "new-user-username", placeholder: "nazwa" }),
                            $$("input", { type: "text", id: "new-user-password", placeholder: "hasło" }),
                        ]),
                        $$("button", { className: "cool button", onclick: onAddUser }, [
                            Icon({ path: "icons/user_add.png" }),
                            $$("span", { innerText: "stwórz" })
                        ])
                    ])
                ])
            ])
            : $$("span");
    });

    return $$("div", {}, [
        $$("fieldset", {}, [
            $$("legend", { innerText: "dane o użytkowniku" }),
            $$("div", {}, [
                $$("span", { innerText: "id użytkownika:", style: "font-weight: bold; margin-right: 5px"}),
                userIdElem.elem
            ])
        ]),
        $$("fieldset", {}, [
            $$("legend", { innerText: "ustawienia użytkownika"}),
            $$("div", {}, [
                $$("div", { innerText: "nazwa użytkownika", style: "font-weight: bold" }),
                $$("div", { className: "text-box" }, [
                    usernameElem.elem
                ]),
                $$("button", { className: "cool button", onclick: onChangeUsername }, [
                    Icon({ path: "icons/textfield_rename.png" }),
                    $$("span", { innerText: "zmień nazwę" })
                ])
            ]),
            $$("div", { innerHTML: "&nbsp;" }),
            $$("div", {}, [
                $$("div", { innerText: "hasło", style: "font-weight: bold" }),
                $$("div", { className: "text-box" }, [
                    $$("input", { type: "password", id: "current-password", placeholder: "aktualne hasło" }),
                    $$("input", { type: "password", id: "new-password", placeholder: "nowe hasło" }),
                    $$("input", { type: "password", id: "new-password-confirm", placeholder: "potwierdź hasło" })
                ]),
                $$("button", { className: "cool button", onclick: onChangePassword }, [
                    Icon({ path: "icons/key_go.png" }),
                    $$("span", { innerText: "zmień hasło" })
                ])
            ])
        ]),
        adminPanel.elem
    ]);
}

export default Settings;