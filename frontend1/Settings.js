import { createAdminChangeUserActivated, createAdminChangeUserPassword, createAdminCreateNewUser, createAdminGetAllUsers, createAdminRenameUser, createClientChangeMyPassword, createClientRenameMe, createWhoAmIMessage } from "./createMessages.js";
import { $$, regeneratable } from "./elemMake.js"
import { sendSocket } from "./socket.js";
import Icon from "./Icon.js";
import { createError, createNotification } from "./errorManager.js";
import { makeid, sha256 } from "./utils.js";
import ClickableIcon from "./ClickableIcon.js";

const Settings = () => {
    const userIdElem = regeneratable(({ userId = "" }) => 
        $$("span", { style: "font-family: monospace", innerText: userId }));

    const usernameElem = regeneratable(({ username = "" }) =>
        $$("input", { value: username, placeholder: "nazwa użytkownika" }));

    const redo = () => sendSocket(createWhoAmIMessage())
        .then(whoAmI => {
            userIdElem.regenerate({ userId: whoAmI.userId });
            usernameElem.regenerate({ username: whoAmI.userName });
            adminPanel.regenerate({ isAdmin: whoAmI.isAdmin });

            if(whoAmI.isAdmin === true) {
                sendSocket(createAdminGetAllUsers())
                    .then(data => allUsers.regenerate({ users: data.users }));
            }
        });
    redo();

    const onChangeUsername = () => {
        const username = usernameElem.elem.value;

        sendSocket(createClientRenameMe(username))
            .then(data => {
                createNotification("udalo sie zmienic nazwe");
                redo();
            });
    }

    const onChangePassword = () => {
        (async () => {
            const currentPassword = document.querySelector("#current-password").value;

            const newPassword = document.querySelector("#new-password").value;
            const newPasswordConfirm = document.querySelector("#new-password-confirm").value;

            if(newPassword != newPasswordConfirm){
                return { success: false };
            }

            const salt = makeid(128);
            const sha256SaltedOldPassword = await sha256(await sha256(currentPassword) + salt);
            const sha256NewPassword = await sha256(newPassword);

            return { success: true, data: { salt, sha256NewPassword, sha256SaltedOldPassword } };

        })().then(({ success, data }) => {
            if(!success) {
                createError("hasla musza byc takie same")
            } else {
                sendSocket(createClientChangeMyPassword(data.sha256SaltedOldPassword, data.salt, data.sha256NewPassword))
                    .then(data => {
                        createNotification("udalo sie zmienic haslo");
                        redo();
                    });
            }
        });
    }

    const onAddUser = () => {
        const username = document.querySelector("#new-user-username").value;
        const password = document.querySelector("#new-user-password").value;

        sendSocket(createAdminCreateNewUser(username, password, false))
            .then(data => {
                createNotification("udalo sie stworzyc uzytkownika");
                redo();
            })
    }

    const onChangeUserActivated = (userId, activated) => {
        sendSocket(createAdminChangeUserActivated(userId, activated))
            .then(_ => {
                createNotification("udalo sie " + (activated ? "aktywowac" : "deaktywowac") + " uzytkownika")
                redo();
            });
    }

    const onUserChangePassword = userId => {
        const newPassword = prompt("nowe haslo?");
        sha256(newPassword).then(newPasswordHashed => 
            sendSocket(createAdminChangeUserPassword(userId, newPasswordHashed))
                .then(_ => {
                    createNotification("udalo sie zmienic haslo uzytkownika")
                    redo();
                }));
    }

    const onChangeUserName = userId => {
        const newName = prompt("nowa nazwa?");
        sendSocket(createAdminRenameUser(userId, newName))
            .then(_ => {
                createNotification("udalo sie zmienic nazwe uzytkownika")
                redo();
            });
    }

    const allUsers = regeneratable(({ users = [] }) => {
        const getUserIcon = isAdmin => isAdmin
            ? Icon({ path: "icons/shield.png", title: "admin" })
            : Icon({ path: "icons/user.png", title: "zwykly uzytkownik" });

        const getActivateDeactivateIconAction = (userId, activated) => ClickableIcon({ 
                path: activated ? "icons/cog_delete.png" : "icons/cog_add.png",
                title: activated ? "dezaktywuj" : "aktywuj",
                onclick: () => onChangeUserActivated(userId, !activated)
            });

        return $$("div", {}, 
            users.map(user => $$("div", {}, [
                ClickableIcon({ 
                    path: "icons/key.png", 
                    title: "zmien haslo" ,
                    onclick: () => onUserChangePassword(user.id)
                }),
                ClickableIcon({ 
                    path: "icons/user_edit.png", 
                    title: "zmien nazwe",
                    onclick: () => onChangeUserName(user.id)
                }),
                getActivateDeactivateIconAction(user.id, user.activated),
                $$("span", { innerHTML: "&nbsp;", style: "margin-right: 5px" }),
                getUserIcon(user.isAdmin),
                $$("span", { innerText: user.name, style: user.activated ? null : "text-decoration: line-through" })
            ])));
    })

    const adminPanel = regeneratable(({ isAdmin = false }) => {
        return isAdmin
            ? $$("div", { }, [
                $$("div", {}, [
                    Icon({ path: "icons/accept.png" }),
                    $$("span", { innerText: "jestes administratorem!"})
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
                ]),
                $$("fieldset", {}, [
                    $$("legend", { innerText: "wszyscy użytkownicy"}),
                    allUsers.elem
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