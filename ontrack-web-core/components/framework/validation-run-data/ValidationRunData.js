import {lazy, useEffect, useState} from "react";
import {FaSpinner} from "react-icons/fa";

export default function ValidationRunData({data}) {
    const [loadedValidationRunData, setLoadedValidationRunData] = useState(<FaSpinner/>)

    const shortTypeName = data ? data.descriptor.id.slice("net.nemerosa.ontrack.extension.".length) : ''

    const importValidationRunData = () => lazy(() =>
        import(`./${shortTypeName}`).catch(() => {
            console.warn(`Undefined validation run type: ${shortTypeName}`)
            return import(`./default.js`);
        })
    )

    useEffect(() => {
        if (data) {
            const loadValidationRunData = async () => {
                const LoadedValidationRunData = await importValidationRunData()
                setLoadedValidationRunData(<LoadedValidationRunData {...data.data}/>)
            }
            loadValidationRunData().then(() => {
            })
        } else {
            setLoadedValidationRunData('')
        }
    }, [data])

    return (
        <>
            {loadedValidationRunData}
        </>
    )
}