import {createContext, useContext} from "react";
import {message} from "antd";

export const MessageContext = createContext({messageApi: null})

export const useMessageApi = () => useContext(MessageContext).messageApi

export default function MessageContextProvider({children}) {
    const [messageApi, contextHolder] = message.useMessage()

    const context = {messageApi}

    return (
        <>
            {contextHolder}
            <MessageContext.Provider value={context}>
                {children}
            </MessageContext.Provider>
        </>
    )
}