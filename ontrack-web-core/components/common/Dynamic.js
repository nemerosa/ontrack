import {Spin} from "antd";
import {lazy, Suspense} from "react";

// See https://react.dev/reference/react/lazy

export default function useDynamic({path, props, errorMessage}, dependencies = []) {

    const Component = lazy(() => import(`../${path}`))

    return <Suspense
        fallback={
            <Spin size="small"/>
        }
    >
        <Component {...props}/>
    </Suspense>

}

export function Dynamic({path, props, errorMessage, dependencies = []}) {
    return (
        <>
            {
                useDynamic({path, props, errorMessage}, dependencies)
            }
        </>
    )
}
