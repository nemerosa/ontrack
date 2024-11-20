import {useState} from "react";

export function useRefresh() {
    const [refreshCount, setRefreshCount] = useState(0)

    return [
        refreshCount,
        () => {
            setRefreshCount(count => count + 1)
        },
    ]
}
