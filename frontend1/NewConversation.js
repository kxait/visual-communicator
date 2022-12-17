import { createCreateConversationMessage, createGetAvailableMessageRecipientsMessage, createGetUsersByIdOrPartOfName, createWhoAmIMessage } from "./createMessages.js";
import { $$, regeneratable } from "./elemMake.js"
import { sendSocket } from "./socket.js";

const NewConversation = ({ onCreated }) => {
    let recipients = [];
    let availableRecipientUsers = [];
    let searchedRecipients = [];
    let userEnteredConversationName = "";

    const fixConversationName = () => {
        if(recipients.length == 1) {
            userEnteredConversationName = conversationNameInput.elem.value;
            conversationNameInput.regenerate({ conversationName: recipients[0].username, disabled: true });
        }else{
            conversationNameInput.regenerate({ conversationName: userEnteredConversationName, disabled: false });
        }
    }

    const removeRecipient = recipient => {
        recipients = recipients.filter(r => r != recipient);
        availableRecipientUsers.push(recipient);
        addedRecipients.regenerate({ recipients });
        availableRecipients.regenerate({ recipients: availableRecipientUsers });
        fixConversationName();
    }

    const addedRecipients = regeneratable(({ recipients = [] }) => {
        return $$("div", {}, recipients.length == 0
            ? [ $$("span", { innerHTML: "&nbsp;&nbsp;nikt!" }) ]
            : recipients.map(
                r => $$("div", { style: "cursor: pointer; margin-bottom: 5px", onclick: () => removeRecipient(r)}, [
                    $$("img", { src: "icons/user_delete.png", style: "vertical-align: sub; margin-right: 5px" }),
                    $$("span", { innerText: r.username, style: "font-weight: bold"}),
                    $$("span", { innerText: r.id, style: "vertical-align: middle; margin-left: 5px; font-size: 0.9em; font-family: monospace" })
                ])
            ));
    })

    const addRecipient = recipient => {
        if(recipients.findIndex(r => r.id == recipient.id) !== -1)
            return;
        recipients.push(recipient);
        availableRecipientUsers = availableRecipientUsers.filter(r => r != recipient);
        addedRecipients.regenerate({ recipients });
        availableRecipients.regenerate({ recipients: availableRecipientUsers });
        fixConversationName();
    }
    
    const availableRecipients = regeneratable(({ recipients = [] }) => {
        return $$("div", {}, recipients.length == 0
            ? [ $$("span", { innerHTML: "&nbsp;&nbsp;nikt!" }) ]
            : recipients.map(
                r => $$("div", { style: "cursor: pointer; margin-bottom: 5px", onclick: () => addRecipient(r)}, [
                    $$("img", { src: "icons/user_add.png", style: "vertical-align: sub; margin-right: 5px" }),
                    $$("span", { innerText: r.username, style: "font-weight: bold"}),
                    $$("span", { innerText: r.id, style: "vertical-align: middle; margin-left: 5px; font-size: 0.9em; font-family: monospace" })
                ])
            ));
    })

    const conversationNameInput = regeneratable(({ conversationName = "", disabled = false}) => {
        return $$("input", { 
            type: "text", 
            placeholder: "nazwa konwersacji", 
            value: conversationName, 
            disabled, 
            oninput: () => {
                userEnteredConversationName = conversationNameInput.elem.value;
            }})
    })

    const searchedRecipientsElem = regeneratable(({ recipients = [] }) => {
        return recipients.length == 0
            ? $$("span")
            : $$("div", {}, recipients.map(
                r => $$("div", { style: "cursor: pointer; margin-bottom: 5px", onclick: () => addRecipient(r)}, [
                    $$("img", { src: "icons/user_add.png", style: "vertical-align: sub; margin-right: 5px" }),
                    $$("span", { innerText: r.username, style: "font-weight: bold"}),
                    $$("span", { innerText: r.id, style: "vertical-align: middle; margin-left: 5px; font-size: 0.9em; font-family: monospace" })
                ]))
            )
    });

    const searchUser = () => {
        const input = searchInput.value;
        sendSocket(createGetUsersByIdOrPartOfName(input))
            .then(data => {
                searchedRecipients = data.recipients
                    .filter(r => availableRecipientUsers.findIndex(rr => rr.id == r.id) == -1)
                    .filter(r => recipients.findIndex(rr => rr.id == r.id) == -1);
                searchedRecipientsElem.regenerate({ recipients: searchedRecipients });
            })
    }

    const createConversation = () => {
        const recipientIds = recipients.map(r => r.id);
        const name = conversationNameInput.elem.value;

        if(recipientIds.length == 0 || name == "")
            return;

        sendSocket(createWhoAmIMessage())
            .then(data => {
                const id = data.userId;

                sendSocket(createCreateConversationMessage([ ...recipientIds, id], name))
                    .then(data => onCreated(data))
                    .catch(console.error);
            })
    }

    const searchInput = $$("input", { type: "text", placeholder: "wyszukaj użytkownika", oninput: searchUser});
    const createButton = $$("div", {className: "cool button", style: "display: flex; margin: 0 5px 0 5px ", onclick: createConversation}, [
        $$("img", { 
            src: "icons/accept.png",
            alt: "stwórz", 
            style: `
                height: 16px;
                align-self: center; 
             `
        }),
        $$("span", { innerText: "stwórz", style: "align-self: center; margin-left: 5px" })
    ]);

    sendSocket(createGetAvailableMessageRecipientsMessage())
        .then(data => {
            availableRecipientUsers = data.availableRecipients;
            availableRecipients.regenerate({ recipients: availableRecipientUsers });
        }).catch(e => console.error(e));

    return $$("div", { id: "new-conversation"}, [
        $$("div", { className: "text-box", style: "display: flex" }, [
            conversationNameInput.elem,
            createButton
        ]),
        $$("div", {}, [
        ]),
        $$("div", { className: "text-box" }, [
            searchInput
        ]),
        $$("div", { id: "search-results" }, [ searchedRecipientsElem.elem ]),
        $$("div", { innerText: "dodani:", style: "line-height: 2em"}),
        $$("div", { id: "recipients" }, [ addedRecipients.elem ]),
        $$("div", { innerText: "dostępni:", style: "line-height: 2em"}),
        $$("div", { id: "available-recipients" }, [ availableRecipients.elem ]),
    ]);
}

export default NewConversation