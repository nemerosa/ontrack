import {Skeleton} from "antd";

export default function LoadingContainer({loading, className, style, children}) {
    return (
        <>
            <Skeleton className={className} style={style} active loading={loading}>
                {children}
            </Skeleton>
        </>
    )
}