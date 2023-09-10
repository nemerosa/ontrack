import ErrorBoundary from "@components/common/ErrorBoundary";
import {Alert, Spin} from "antd";
import {lazy, useEffect, useState} from "react";

export default function useDynamic({path, props, errorMessage}, dependencies = []) {

    const importComponent = () => lazy(() =>
        import(`../${path}`)
    )

    const [loadedComponent, setLoadedComponent] = useState(<Spin size="small"/>)

    useEffect(() => {
        if (props) {
            const loadComponent = async () => {
                try {
                    const LoadedComponent = await importComponent()
                    setLoadedComponent(<LoadedComponent {...props}/>)
                } catch (any) {
                    console.warn(errorMessage)
                    setLoadedComponent(<Alert
                        type="error"
                        message={errorMessage}
                    />)
                }
            }
            loadComponent().then(() => {
            })
        }
    }, dependencies)

    return (
        <ErrorBoundary fallback={
            <Alert
                type="error"
                message={errorMessage}
            />
        }>
            {loadedComponent}
        </ErrorBoundary>
    )
}