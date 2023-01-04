import Error from "./Error.js"
import Notification from "./Notification.js";

const createError = text => {
    const error = Error({ text, onclick: () => error.remove() });

    const errorContainer = document.querySelector("#error-container");
    errorContainer.appendChild(error);

    setTimeout(() => error.remove(), 2000);
}

const createNotification = ({ text, iconSrc = "icons/accept.png" }) => {
    const notification = Notification({ text, onclick: () => notification.remove(), iconSrc });

    const errorContainer = document.querySelector("#error-container");
    errorContainer.appendChild(notification);

    setTimeout(() => notification.remove(), 2000);
}

export { createError, createNotification }