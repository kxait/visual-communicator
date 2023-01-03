import ClickableIcon from "./ClickableIcon.js";
import CoolButton from "./CoolButton.js";
import { $$, regeneratable } from "./elemMake.js";
import Icon from "./Icon.js";
import { MainPanelState, setMainPanelState } from "./mainPanelState.js";
import { state } from "./state.js";
import { userNameById } from "./userNameById.js";
import { later } from "./utils.js";

const ConversationProfile = ({ conversationId }) => {
  const conversation = state().conversations.filter(
    (conversation) => conversation.id === conversationId
  )[0];

  const isAuthor = conversation.author === state().userId;

  const members = regeneratable(({ members = [] }) => {
    return $$(
      "div",
      {},
      members.map((member) => {
        const usernameSpan = regeneratable(({ username = "" }) =>
          $$("span", {
            style: "border-bottom: 1px dotted white; cursor: pointer",
            innerText: username,
            onclick: () => {
              setMainPanelState({
                mainPanelState: MainPanelState.userProfile,
                additionalData: { userId: member },
              });
            },
          })
        );

        userNameById(member).then((username) =>
          usernameSpan.regenerate({ username })
        );

        return $$("div", {}, [
          ...(isAuthor
            ? [ClickableIcon({ path: "icons/cancel.png", title: "usuń", onclick: () => alert(":)") })]
            : []),
          ...[Icon({ path: "icons/user.png" }), usernameSpan.elem],
        ]);
      })
    );
  });

  members.regenerate({ members: conversation.recipients });

  const onRenameConversation = () => {
    const name = conversationNameInput.value;

    console.log(name);
  }

  const onLeave = () => {

  }

  const onDelete = () => {

  }

  const conversationNameInput = $$("input", {
    type: "text",
    value: conversation.name,
    style: `
          font-size: 1.2em;
          background-color: #333;
          color: white;
          width: 100%;
          padding: 5px;
          margin-bottom: 5px;
      `,
    disabled: !isAuthor,
  })

  return $$("div", {}, [
    conversationNameInput,
    $$("div", { style: "display: flex; margin-bottom: 20px" }, [
      ...(isAuthor 
            ? [ CoolButton({
                src: "icons/textfield_rename.png",
                text: "zmień nazwę",
                style: "margin-right: 10px",
                onclick: onRenameConversation
            }),
            CoolButton({
                src: "icons/cancel.png",
                text: "usuń",
                style: "margin-right: 10px",
                onclick: onDelete
              }) ]
            : [ CoolButton({
                src: "icons/door_out.png",
                text: "opuść",
                style: "margin-right: 10px",
                onclick: onLeave
              }) ])
    ]),
    later(userNameById(conversation.author), data => 
        $$("p", { innerText: `konwersacja stworzona przez ${data}`
    })).elem,
    $$("fieldset", { style: "width: fit-content; " }, [
      $$("legend", { innerText: "członkowie" }),
      members.elem,
    ]),
  ]);
};

export default ConversationProfile;
