import {lazy, useEffect, useState} from "react";
import {FaSpinner} from "react-icons/fa";

export default function ValidationRunData({data}) {
    if (data) {
        const shortTypeName = data.descriptor.id.slice("net.nemerosa.ontrack.extension.".length)

        const importValidationRunData = () => lazy(() =>
            import(`./${shortTypeName}`).catch(() => {
                console.warn(`Undefined validation run type: ${shortTypeName}`)
                return import(`./default.js`);
            })
        )

        const [loadedValidationRunData, setLoadedValidationRunData] = useState(<FaSpinner/>)

        useEffect(() => {
            if (data) {
                const loadValidationRunData = async () => {
                    const LoadedValidationRunData = await importValidationRunData()
                    setLoadedValidationRunData(<LoadedValidationRunData {...data.data}/>)
                }
                loadValidationRunData().then(() => {
                })
            }
        }, [data])

        return (
            <>
                {loadedValidationRunData}
            </>
        )
    } else {
        return ''
    }
}