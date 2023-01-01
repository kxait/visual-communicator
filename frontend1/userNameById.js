import { createGetUsernameOfUserIdMessage } from "./createMessages.js";
import { sendSocket } from "./socket.js";

let userNamesById = {};

const userNameById = async userId => {
    if(Object.keys(userNamesById).includes(userId)) {
        const value = userNamesById[userId];

        const isPromise = typeof value === 'object' && typeof value.then === 'function';

        if(isPromise)
            await value;

        return userNamesById[userId];
    }

    const promise = (async () => {
        const result = await sendSocket(createGetUsernameOfUserIdMessage(userId));
        const username = result.username;
        userNamesById[userId] = username;
        return username;
    })()
    userNamesById[userId] = promise;
    await promise;
    return userNamesById[userId];
};

export { userNameById };