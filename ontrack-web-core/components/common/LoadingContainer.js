import {Skeleton} from "antd";

export default function LoadingContainer({loading, error, className, style, children}) {
    return (
        <>
            <Skeleton className={className} style={style} active loading={loading}>
                {children}
                {/*{(!error || error.length === 0) && children}*/}
                {/*{(error && error.length > 0) && JSON.stringify({error})}*/}
            </Skeleton>
        </>
    )
}