import { createClientGetProfileData, createClientSetProfileData } from "./createMessages.js"
import { sendSocket } from "./socket.js"

const profileData = async () => {
    var profileData = await sendSocket(createClientGetProfileData());

    const data = (profileData.profileData ?? "") === "" ? "{}" : profileData.profileData

    return JSON.parse(data);
}

const setProfileData = async pd => {
    await sendSocket(createClientSetProfileData(JSON.stringify(pd)));
}

const patchProfileData = async obj => {
    const pd = await profileData();
    await setProfileData({
        ...pd,
        ...obj
    });
}

export {
    profileData,
    setProfileData,
    patchProfileData
}