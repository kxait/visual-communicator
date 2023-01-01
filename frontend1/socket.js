import { setState, state } from './state.js'
import {createPubsub} from './pubsub.js'
import { messageTypes } from './messageTypes.js';
import { createError } from './errorManager.js';

const { publish, subscribe } = createPubsub();

const makeSocket = url => new Promise((res, rej) => {    
    setState({ socket: new WebSocket(url), wasOpen: false })
    const s = state();
    s.socket.addEventListener('open', e => {
        console.debug('socket open');
        setState({ wasOpen: true });
        res();
    });
    s.socket.addEventListener('close', e => {
        console.debug('socket closed');
        if(state().wasOpen)
            // reload page in case we got logged out
            location.reload();
        rej();
    });
    s.socket.addEventListener('message', e => {
        var data = JSON.parse(e.data);
        publish(data);
    });
});

const sendSocket = data => new Promise((res, rej) => {
    const id = JSON.parse(data).id;
    const unsubscribe = subscribe(data => { 
        if(data == null || data.id == null) {
            rej(data);
        }
        if(data.id === id && data.type === messageTypes.serverErr) {
            rej(data);
            createError(data.error.content);
        }
        if(data != null && data.id === id) {
            unsubscribe();
            res(data);
        }
        
    });
    state().socket.send(data);
});

const subscribeSocket = subscribe;
export {
    makeSocket,
    sendSocket,
    subscribeSocket
}