import {Space, Spin, Tooltip, Typography} from "antd";
import {lazy, Suspense} from "react";
import {FaTimes} from "react-icons/fa";
import {ErrorBoundary} from "react-error-boundary";

// See https://react.dev/reference/react/lazy

function LoadingError({path}) {
    return (
        <Tooltip title={`Could not load dynamic component at ${path}. This is a defect.`}>
            <Space>
                <FaTimes color="red"/>
                <Typography.Text>Error</Typography.Text>
            </Space>
        </Tooltip>
    )
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
