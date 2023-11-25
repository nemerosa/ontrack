import {createContext} from "react";

export const WidgetContext = createContext(null)
export const WidgetDispatchContext = createContext(null)

export const widgetReducer = (widgetContext, action) => {
    switch (action.type) {
        case 'edit': {
            return {
                ...widgetContext,
                editionMode: true,
            }
        }
        case 'cancel': {
            widgetContext.editionForm.resetFields()
            return {
                ...widgetContext,
                editionMode: false,
            }
        }
        case 'done': {
            widgetContext.editionForm.resetFields()
            return {
                ...widgetContext,
                widget: {
                    ...widgetContext.widget,
                    config: action.config,
                },
                editionMode: false,
            }
        }
        default: {
            throw Error('Unknown action: ' + action.type);
        }
    }
}

export const widgetFormSubmit = async (widgetContext, widgetDispatch, dashboardDispatch) => {
    const values = await widgetContext.editionForm.validateFields()
    dashboardDispatch({
        type: 'saveWidgetConfig',
        widget: widgetContext.widget,
        config: values,
    })
    widgetDispatch({
        type: 'done',
        config: values,
    })
}