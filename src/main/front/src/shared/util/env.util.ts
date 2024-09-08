
// TODO: comment me!
// TODO: add profile information here!

export const applicationEnv = {
    profile: process.env.NODE_ENV,
    apiBasePath: process.env.REACT_APP_API_BASE_PATH as string,
    websocketBasePath: process.env.WEBSOCKET_BASE_PATH as string,
};
console.debug(`Running '${applicationEnv.profile}' profile`);
