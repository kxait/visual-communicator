import { createAdminChangeUserActivated, createAdminChangeUserPassword, createAdminCreateNewUser, createAdminGetAllUsers, createAdminRenameUser, createClientChangeMyPassword, createClientRenameMe, createWhoAmIMessage } from "./createMessages.js";
import { $$, regeneratable } from "./elemMake.js"
import { sendSocket } from "./socket.js";
import Icon from "./Icon.js";
import { createError, createNotification } from "./errorManager.js";
import { makeid, sha256 } from "./utils.js";
import ClickableIcon from "./ClickableIcon.js";
import CoolButton from "./CoolButton.js";
import { MainPanelState, setMainPanelState } from "./mainPanelState.js";

const Settings = () => {
    const userIdElem = regeneratable(({ userId = "" }) => 
        $$("span", { style: "font-family: monospace", innerText: userId }));

    const usernameElem = regeneratable(({ username = "" }) =>
        $$("input", { type: "text", maxLength: "30", value: username, placeholder: "Nazwa użytkownika" }));

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
                createNotification({ text: "Udało się zmienić nazwę." });
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
                createError("Hasła muszą być takie same.")
            } else {
                sendSocket(createClientChangeMyPassword(data.sha256SaltedOldPassword, data.salt, data.sha256NewPassword))
                    .then(data => {
                        createNotification({ text: "Udało się zmienić hasło." });
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
                createNotification({ text: "Udało się stworzyć użytkownika." });
                redo();
            })
    }

    const onChangeUserActivated = (userId, activated) => {
        sendSocket(createAdminChangeUserActivated(userId, activated))
            .then(_ => {
                createNotification({ text: "Udało się " + (activated ? "aktywować" : "deaktywować") + " użytkownika." })
                redo();
            });
    }

    const onUserChangePassword = userId => {
        const newPassword = prompt("Nowe hasło?");
        sha256(newPassword).then(newPasswordHashed => 
            sendSocket(createAdminChangeUserPassword(userId, newPasswordHashed))
                .then(_ => {
                    createNotification({ text: "Udało się zmienić hasło użytkownika." })
                    redo();
                }));
    }

    const onChangeUserName = userId => {
        const newName = prompt("Nowa nazwa?");
        sendSocket(createAdminRenameUser(userId, newName))
            .then(_ => {
                createNotification({ text: "Udało się zmienić nazwę użytkownika." })
                redo();
            });
    }

    const allUsers = regeneratable(({ users = [] }) => {
        const getUserIcon = isAdmin => isAdmin
            ? Icon({ path: "icons/shield.png", title: "Admin" })
            : Icon({ path: "icons/user.png", title: "Zwykły użytkownik" });

        const getActivateDeactivateIconAction = (userId, activated) => ClickableIcon({ 
                path: activated ? "icons/cog_delete.png" : "icons/cog_add.png",
                title: activated ? "Dezaktywuj" : "Aktywuj",
                onclick: () => onChangeUserActivated(userId, !activated)
            });

        return $$("div", {}, 
            users.map(user => $$("div", {}, [
                ClickableIcon({ 
                    path: "icons/key.png", 
                    title: "Zmień hasło" ,
                    onclick: () => onUserChangePassword(user.id)
                }),
                ClickableIcon({ 
                    path: "icons/user_edit.png", 
                    title: "Zmień nazwę",
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
                    $$("span", { innerText: "Jesteś administratorem!"})
                ]),
                CoolButton({ style: "width: fit-content", onclick: () => {
                    setMainPanelState({ mainPanelState: MainPanelState.logs });
                }, text: "logi", src: "icons/application_view_list.png" }),
                $$("fieldset", {}, [
                    $$("legend", { innerText: "Ustawienia administratora" }),
                    $$("div", {}, [
                        $$("div", { innerText: "Stwórz użytkownika", style: "font-weight: bold" }),
                        $$("div", { className: "text-box" }, [
                            $$("input", { type: "text", id: "new-user-username", placeholder: "Nazwa" }),
                            $$("input", { type: "text", id: "new-user-password", placeholder: "Hasło" }),
                        ]),
                        $$("button", { className: "cool button", onclick: onAddUser }, [
                            Icon({ path: "icons/user_add.png" }),
                            $$("span", { innerText: "Stwórz" })
                        ])
                    ])
                ]),
                $$("fieldset", {}, [
                    $$("legend", { innerText: "Wszyscy użytkownicy"}),
                    allUsers.elem
                ])                
            ])
            : $$("span");
    });

    return $$("div", {}, [
        $$("fieldset", {}, [
            $$("legend", { innerText: "Dane o użytkowniku" }),
            $$("div", {}, [
                $$("span", { innerText: "ID użytkownika:", style: "font-weight: bold; margin-right: 5px"}),
                userIdElem.elem
            ])
        ]),
        $$("fieldset", {}, [
            $$("legend", { innerText: "Ustawienia użytkownika"}),
            $$("div", {}, [
                $$("div", { innerText: "Nazwa użytkownika", style: "font-weight: bold" }),
                $$("div", { className: "text-box" }, [
                    usernameElem.elem
                ]),
                $$("button", { className: "cool button", onclick: onChangeUsername }, [
                    Icon({ path: "icons/textfield_rename.png" }),
                    $$("span", { innerText: "Zmień nazwę" })
                ])
            ]),
            $$("div", { innerHTML: "&nbsp;" }),
            $$("div", {}, [
                $$("div", { innerText: "Hasło", style: "font-weight: bold" }),
                $$("div", { className: "text-box" }, [
                    $$("input", { type: "password", id: "current-password", placeholder: "Aktualne hasło" }),
                    $$("input", { type: "password", id: "new-password", placeholder: "Nowe hasło" }),
                    $$("input", { type: "password", id: "new-password-confirm", placeholder: "Potwierdź hasło" })
                ]),
                $$("button", { className: "cool button", onclick: onChangePassword }, [
                    Icon({ path: "icons/key_go.png" }),
                    $$("span", { innerText: "Zmień hasło" })
                ])
            ])
        ]),
        adminPanel.elem
    ]);
}

export default Settings;