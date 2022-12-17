import { $$ } from "./elemMake.js"

const Login = ({ onLogin }) => {
    return $$("div", { id: "login-screen", style: `
        margin: 0 auto;
        width: fit-content;
    ` }, [
        $$("h1", { id: "logo-text", innerText: "Visual Communicator", style: "text-align: center" }, [
            $$("div", { innerHTML: '&nbsp;' }),
            $$("form", { onsubmit: () => false, style: "font-size: 0.5em" }, [
                $$("div", {}, [
                    $$("input", { style: "margin: 5px; width:100%; font-size: inherit; padding: 5px", type: "text", id: "input-username", placeholder: "Nazwa użytkownika"})
                ]),
                $$("div", {}, [
                    $$("input", { style: "margin: 5px; width: 100%; font-size: inherit; padding: 5px", type: "password", id: "input-password", placeholder: "Hasło"})
                ]),
                $$("div", {}, [
                    $$("button", { style: "margin: 5px; width: 100%; font-size: inherit; padding: 5px", type: "submit", onclick: onLogin, innerText: "Zaloguj się" })
                ]),
            ])
        ])
    ])
}

export default Login;