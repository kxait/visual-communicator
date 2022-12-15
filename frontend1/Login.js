import { $$ } from "./elemMake.js"

const Login = ({ onLogin }) => {
    return $$("div", { id: "login-screen", style: `
        margin: 0 auto;
        width: fit-content;
    ` }, [
        $$("h3", { id: "logo-text", innerText: "Visual Communicator" }, [
            $$("div", { innerHTML: '&nbsp;' }),
            $$("form", { onsubmit: () => false, style: "text-align: center;" }, [
                $$("div", {}, [
                    $$("input", { type: "text", id: "input-username", placeholder: "Nazwa użytkownika"})
                ]),
                $$("div", {}, [
                    $$("input", { type: "text", id: "input-password", placeholder: "Hasło"})
                ]),
                $$("div", {}, [
                    $$("button", { type: "submit", onclick: onLogin, innerText: "Zaloguj się" })
                ]),
            ])
        ])
    ])
}

export default Login;