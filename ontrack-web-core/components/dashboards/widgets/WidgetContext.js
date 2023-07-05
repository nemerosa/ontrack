import {createContext} from "react";
import graphQLCall, {getUserErrors} from "@client/graphQLCall";
import {gql} from "graphql-request";

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
        // case 'save': {
        //     widget.editionForm.validateFields().then(values => {
        //         // TODO Submit the values
        //     }).then(data => {
        //         const errors = getUserErrors(data.xxx)
        //         if (errors) {
        //
        //         }
        //     })
        //     // No change
        //     return widget
        // }
        case 'cancel': {
            widget.editionForm.resetFields()
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

export const widgetFormSubmit = async (widgetContext, widgetDispatch, dashboardDispatch) => {
    const values = await widgetContext.editionForm.validateFields()

    if (widgetContext.dashboard.builtIn) {
        // Saving as a new dashboard
        //
        // We need to create a copy of the current dashboard
        // and start the "save" process
        //
        // This can be done using the dashboard dispatch
        dashboardDispatch({
            type: 'save-as',
            widget: {
                uuid: widgetContext.widget.uuid,
                config: values,
            }
        })
    } else {
        // TODO Updating
    }

    // const variables = {
    //     dashboardKey: widgetContext.dashboard.key,
    //     widgetUuid: widgetContext.widget.uuid,
    //     config: values,
    // }
    // const data = await graphQLCall(
    //     gql`
    //         mutation UpdateWidgetConfig(
    //             $dashboardKey: String!,
    //             $widgetUuid: String!,
    //             $config: JSON!,
    //         ) {
    //             updateWidgetConfig(input: {
    //                 dashboardKey: $dashboardKey,
    //                 widgetUuid: $widgetUuid,
    //                 config: $config,
    //             }) {
    //                 errors {
    //                     message
    //                     exception
    //                 }
    //                 widget {
    //                     uuid
    //                     key
    //                     config
    //                 }
    //             }
    //         }
    //     `, variables
    // )
    // const errors = getUserErrors(data.xxx)
    // if (errors) {
    //     // TODO Displays the errors form through a reducer action
    // } else {
    //     // TODO Refreshing the widget
    // }
}