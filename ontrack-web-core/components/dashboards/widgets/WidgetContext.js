import {createContext} from "react";

export const WidgetContext = createContext(null)
export const WidgetDispatchContext = createContext(null)

export const widgetReducer = (widget, action) => {
    switch (action.type) {
        default: {
            throw Error('Unknown action: ' + action.type);
        }
    }
}