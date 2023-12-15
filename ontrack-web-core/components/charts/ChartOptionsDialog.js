import {createContext, useContext, useEffect, useState} from "react";
import ChartOptionsCommand from "@components/charts/ChartOptionsCommand";
import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import SelectChartInterval from "@components/charts/SelectChartInterval";
import {Form} from "antd";
import SelectChartPeriod from "@components/charts/SelectChartPeriod";

const ChartOptionsCommandContext = createContext({
    dialog: {},
    interval: "3m",
    period: "1w",
})

export const useChartOptionsCommand = () => {

    const {dialog, interval, period} = useContext(ChartOptionsCommandContext)

    const onOpen = () => {
        dialog.start({interval, period})
    }

    const command = <ChartOptionsCommand
        interval={interval}
        period={period}
        onClick={onOpen}
    />

    return {
        command,
        interval,
        period,
    }
}

export const ChartOptionsDialog = ({chartOptionsDialog}) => {
    return (
        <>
            <FormDialog dialog={chartOptionsDialog}>
                <Form.Item name="interval"
                           label="Interval"
                           rules={[
                               {
                                   required: true,
                                   message: 'Interval is required.',
                               },
                           ]}
                >
                    <SelectChartInterval/>
                </Form.Item>
                <Form.Item name="period"
                           label="Period"
                           rules={[
                               {
                                   required: true,
                                   message: 'Period is required.',
                               },
                           ]}
                >
                    <SelectChartPeriod/>
                </Form.Item>
            </FormDialog>
        </>
    )
}

export default function StoredChartOptionsCommandContextProvider({id, children}) {

    const [interval, setInterval] = useState("3m")
    const [period, setPeriod] = useState("1w")

    useEffect(() => {
        const value = localStorage.getItem(id)
        if (value) {
            const json = JSON.parse(value)
            setInterval(json.interval ?? "3m")
            setPeriod(json.period ?? "1w")
        }
    }, []);

    const onSuccess = ({interval, period}) => {
        setInterval(interval)
        setPeriod(period)
        // Saves the values into the local storage
        localStorage.setItem(id, JSON.stringify({interval, period}))
    }

    const dialog = useFormDialog({
        onSuccess: onSuccess,
        init: (form, {interval, period}) => {
            form.setFieldsValue({interval, period})
        }
    })

    const context = {
        dialog,
        interval,
        period,
    }

    return (
        <ChartOptionsCommandContext.Provider value={context}>
            {children}
            <ChartOptionsDialog chartOptionsDialog={dialog}/>
        </ChartOptionsCommandContext.Provider>
    )
}