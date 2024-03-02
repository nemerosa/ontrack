import React, {createContext, useContext, useEffect, useRef, useState} from "react";
import {Dropdown, Space, Tooltip, Typography} from "antd";
import {FaSync} from "react-icons/fa";
import SelectableMenuItem from "@components/common/SelectableMenuItem";

export const AutoRefreshContext = createContext({
    autoRefreshEnabled: false,
    toggleEnabled: () => {
    },
    autoRefreshIntervalSeconds: 60,
    setAutoRefreshIntervalSeconds: () => {
    },
})

export function AutoRefreshContextProvider({children, onRefresh}) {

    const autoRefreshIdRef = useRef(0)
    const [autoRefreshEnabled, setAutoRefreshEnabled] = useState(false)
    const [autoRefreshIntervalSeconds, setAutoRefreshIntervalSeconds] = useState(60)
    const autoRefreshIntervalSecondsRef = useRef(autoRefreshIntervalSeconds)

    const toggleEnabled = () => {
        setAutoRefreshEnabled(state => !state)
    }

    const context = {
        autoRefreshEnabled,
        toggleEnabled,
        autoRefreshIntervalSeconds,
        setAutoRefreshIntervalSeconds,
    }

    const refresh = () => {
        if (onRefresh) {
            onRefresh()
        }
    }

    const close = () => {
        if (autoRefreshIdRef.current) {
            clearInterval(autoRefreshIdRef.current)
            autoRefreshIdRef.current = 0
        }
    }

    const startRefresh = () => {
        if (autoRefreshIntervalSecondsRef.current > 0) {
            autoRefreshIdRef.current = setInterval(refresh, autoRefreshIntervalSecondsRef.current * 1000)
        }
    }

    useEffect(() => {
        if (autoRefreshEnabled) {
            if (!autoRefreshIdRef.current) {
                refresh()
                startRefresh()
            }
        } else {
            close()
        }
    }, [autoRefreshEnabled])

    useEffect(() => {
        if (autoRefreshIdRef.current && autoRefreshEnabled) {
            clearInterval(autoRefreshIdRef.current)
            autoRefreshIntervalSecondsRef.current = autoRefreshIntervalSeconds
            startRefresh()
        }
    }, [autoRefreshIntervalSeconds, autoRefreshEnabled]);

    useEffect(() => {
        return () => {
            close()
        }
    }, []);

    return (
        <>
            <AutoRefreshContext.Provider value={context}>
                {children}
            </AutoRefreshContext.Provider>
        </>
    )
}

export function AutoRefreshButton() {

    const autoRefresh = useContext(AutoRefreshContext)

    const allowedValues = [
        [5, "Every 5 seconds"],
        [10, "Every 10 seconds"],
        [30, "Every 30 seconds"],
        [60, "Every minute"],
        [120, "Every 2 minutes"],
        [300, "Every 5 minutes"],
        [600, "Every 10 minutes"],
    ]

    const items = allowedValues.map(([seconds, text]) => ({
        key: String(seconds),
        label: <SelectableMenuItem
            value={autoRefresh.autoRefreshIntervalSeconds === seconds}
            text={text}
        />,
    }))

    const handleMenuClick = (e) => {
        const seconds = Number(e.key)
        autoRefresh.setAutoRefreshIntervalSeconds(seconds)
    }

    const menuProps = {
        items,
        onClick: handleMenuClick,
    }

    const onButtonClick = () => {
        autoRefresh.toggleEnabled()
    }

    return (
        <Dropdown.Button
            menu={menuProps}
            onClick={onButtonClick}
            className={autoRefresh.autoRefreshEnabled ? "ot-auto-refresh ot-auto-refresh-enabled" : "ot-auto-refresh"}
            buttonsRender={([leftButton, rightButton]) => [
                <Tooltip title={
                    autoRefresh.autoRefreshEnabled ? "Auto refresh is enabled. Click to disable." : "No auto refresh. Click to enable it."
                } key="leftButton">
                    {leftButton}
                </Tooltip>,
                <Tooltip title={
                    "Intervals between each refresh."
                } key="rightButton">
                    {rightButton}
                </Tooltip>,
            ]}
        >
            <Space>
                <FaSync/>
                <Typography.Text>Auto refresh</Typography.Text>
            </Space>
        </Dropdown.Button>
    )
}