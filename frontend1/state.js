let _state = {
    socket: null
};

const setState = newState => {
    _state = {..._state, ...newState};
}

const state = () => _state;

export {
    setState, 
    state
}