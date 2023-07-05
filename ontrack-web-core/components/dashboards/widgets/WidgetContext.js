import {createContext} from "react";

export const WidgetContext = createContext(null)
export const WidgetDispatchContext = createContext(null)

export const widgetReducer = (widget, action) => {
    switch (action.type) {
        case 'edit': {
            return {
                ...widget,
                editionMode: true,
            }
        }
        case 'save': {
            return {
                ...widget,
                editionMode: false,
            }
        }
        case 'cancel': {
            return {
                ...widget,
                editionMode: false,
            }
        }
        default: {
            throw Error('Unknown action: ' + action.type);
        }
    }
}