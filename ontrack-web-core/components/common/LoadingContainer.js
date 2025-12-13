import {Alert, Skeleton} from "antd";

export default function LoadingContainer({loading, error, className, style, children}) {
    return (
        <>
            <Skeleton className={className} style={style} active loading={loading}>
                {!error && children}
                {
                    error && <Alert type="error" closable showIcon>
                        {error}
                    </Alert>
                }
            </Skeleton>
        </>
    )
}