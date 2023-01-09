import CoolButton from "./CoolButton.js";
import { $$, regeneratable } from "./elemMake.js";
import { patchProfileData, profileData } from "./profileData.js";
import { userNameById } from "./userNameById.js";

const UserProfile = ({ userId }) => {

    const usernameDiv = regeneratable(({ username = userId }) => 
        $$("div", { innerText: username }));

    userNameById(userId)
        .then(username => usernameDiv.regenerate({ username }));

    const block = async () => {
        let blockedUsers = (await profileData()).blockedUsers ?? [];
        blockedUsers = [userId, ...blockedUsers.filter(uId => uId !== userId)];
        await patchProfileData({ blockedUsers });
        redo();
    }

    const unblock = async () => {
        let blockedUsers = (await profileData()).blockedUsers ?? [];
        blockedUsers = blockedUsers.filter(uId => uId !== userId);
        await patchProfileData({ blockedUsers });
        redo();
    }

    const blockUnblockButton = regeneratable(({ isBlocked = false }) => {
        return isBlocked
            ? CoolButton({ style: "width: fit-content", text: "odblokuj", src: "icons/accept.png", onclick: unblock })
            : CoolButton({ style: "width: fit-content", text: "zablokuj", src: "icons/cancel.png", onclick: block });
    });

    const redo = () => profileData().then(data => {
        const blockedUsers = data.blockedUsers ?? [];
        const isBlocked = blockedUsers.includes(userId);

        blockUnblockButton.regenerate({ isBlocked });
    })
    redo();

    return $$("div", {}, [ 
        usernameDiv.elem,
        blockUnblockButton.elem
    ]);
};

export default UserProfile;