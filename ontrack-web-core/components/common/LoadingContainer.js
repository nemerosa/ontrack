import {Skeleton} from "antd";

export default function LoadingContainer({loading, tip, children}) {
    return (
        <>
            {
                loading &&
                <Skeleton active/>
            }
            {
                !loading && children
            }
        </>
    )
}