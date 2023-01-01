import { createPubsub } from "./pubsub.js";

const MainPanelState = {
    // {}
    welcomeScreen: 0,
    // { conversationId: uuid }
    conversation: 1,
    // {}
    newConversation: 2,
    // {}
    settings: 3,
    // { userId: uuid }
    userProfile: 4,
    // { conversationId: uuid }
    conversationProfile: 5
};

const { publish: setMainPanelState, subscribe: subscribeMainPanelState } = createPubsub();

export { setMainPanelState, subscribeMainPanelState, MainPanelState };