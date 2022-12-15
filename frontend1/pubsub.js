const createMiddlewarePubsub = func => {
    let subscribers = [];
    const subscribe = invocation => {
        subscribers = [...subscribers, invocation];
        return () => subscribers = subscribers.filter(s => s != invocation);
    }
    const publish = data => subscribers.forEach(s => s(func(data)));

    return {
        publish,
        subscribe
    };
}

const createPubsub = () => createMiddlewarePubsub(d => d);

export {
    createPubsub,
    createMiddlewarePubsub
};