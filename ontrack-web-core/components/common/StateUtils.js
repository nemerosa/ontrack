import {useState} from "react";

export const useReloadState = () => {
    const [reloadCount, setReloadCount] = useState(0)
    return [
        reloadCount,
        () => setReloadCount(i => i + 1),
    ]
}