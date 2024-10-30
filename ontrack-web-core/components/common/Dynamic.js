import {Spin} from "antd";
import {lazy, Suspense} from "react";
import {ErrorBoundary} from "react-error-boundary";
import InlineError from "@components/common/InlineError";

// See https://react.dev/reference/react/lazy

function LoadingError({path}) {
    return <InlineError message={`Could not load dynamic component at ${path}. This is a defect.`}/>
}

export default function useDynamic({path, props}, dependencies = []) {

    const Component = lazy(() => import(`../${path}`))

    const logError = (error) => {
        console.log(error)
    }

    return (
        <ErrorBoundary
            fallback={<LoadingError path={path}/>}
            onError={logError}
        >
            <Suspense
                fallback={
                    <Spin size="small"/>
                }
            >
                <Component {...props}/>
            </Suspense>
        </ErrorBoundary>
    )

}

export function Dynamic({path, props, dependencies = []}) {
    return (
        <>
            {
                useDynamic({path, props}, dependencies)
            }
        </>
    )
}
