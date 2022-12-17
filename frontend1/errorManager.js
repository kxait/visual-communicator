import Error from "./Error.js"

const createError = text => {
    const error = Error({ text, onclick: () => error.remove() });

    const errorContainer = document.querySelector("#error-container");
    errorContainer.appendChild(error);

    setTimeout(() => error.remove(), 2000);
}

export { createError }