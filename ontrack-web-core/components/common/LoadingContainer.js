import {Skeleton} from "antd";

export default function LoadingContainer({loading, children}) {
    return (
        <>
            <Skeleton active loading={loading}>
                {children}
            </Skeleton>
        </>
    )
}