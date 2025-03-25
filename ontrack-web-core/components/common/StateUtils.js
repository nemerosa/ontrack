import {useState} from "react";

/**
 * @deprecated Use useRefresh()
 */
export const useReloadState = (config = {callback: undefined}) => {
    const [reloadCount, setReloadCount] = useState(0)

    const reload = () => {
        setReloadCount(i => i + 1)
        if (config.callback) config.callback()
    }

    return [
        reloadCount,
        reload,
    ]
}