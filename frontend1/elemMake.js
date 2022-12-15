const makeElem = (name, props = {}, children = []) => {
    const elem = document.createElement(name);
    Object.assign(elem, props);
    for(const i of children) {
        elem.appendChild(i);
    }
    return elem;
}

const $$ = makeElem;

const makeRoot = content => document
    .querySelector("#root")
    .replaceWith($$("div", { id: "root" }, content));

export { 
    makeRoot,
    makeElem, 
    $$ 
}