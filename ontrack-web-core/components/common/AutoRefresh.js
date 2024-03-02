import React, {createContext, useContext, useEffect, useRef, useState} from "react";
import {Dropdown, Space, Tooltip, Typography} from "antd";
import {FaSync} from "react-icons/fa";

export const AutoRefreshContext = createContext({
    autoRefreshEnabled: false,
    toggleEnabled: () => {
    },
})

export function AutoRefreshContextProvider({children, onRefresh}) {

    const autoRefreshIdRef = useRef(0)
    const [autoRefreshEnabled, setAutoRefreshEnabled] = useState(false)

    const toggleEnabled = () => {
        setAutoRefreshEnabled(state => !state)
    }

    const context = {
        autoRefreshEnabled,
        toggleEnabled,
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

    useEffect(() => {
        if (autoRefreshEnabled) {
            if (!autoRefreshIdRef.current) {
                refresh()
                 // TODO Use the selected interval
                autoRefreshIdRef.current = setInterval(refresh, 5000)
            }
        } else {
            close()
        }
    }, [autoRefreshEnabled])

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

    const items = [
        {
            key: '30',
            label: "Every 30 seconds",
        },
        {
            key: '60',
            label: "Every minutes",
        },
        {
            key: '120',
            label: "Every 2 minutes",
        },
        {
            key: '300',
            label: "Every 5 minutes",
        },
        {
            key: '600',
            label: "Every 10 minutes",
        },
    ]

    const handleMenuClick = (e) => {
        const seconds = Number(e.key)
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