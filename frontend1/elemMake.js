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

    
const regeneratable = (dataGenerator, argsTld = {}) => {
    let data = {
        elem: dataGenerator(argsTld),
        regenerate: (args = {}) => {
            const x = dataGenerator(args);
            data.elem.replaceWith(x);
            data.elem = x;
        }
    }
    return data;
}

const asyncRegeneratable = async (dataGenerator, argsTld = {}) => {
    let data = {
        elem: await dataGenerator(argsTld),
        regenerate: async (args = {}) => {
            const x = await dataGenerator(args);
            data.elem.replaceWith(x);
            data.elem = x;
        }
    }
    return data;
}

export { 
    makeRoot,
    makeElem, 
    $$,
    regeneratable,
    asyncRegeneratable
}