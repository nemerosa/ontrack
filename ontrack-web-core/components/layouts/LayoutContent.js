import {ErrorBoundary} from "react-error-boundary";
import ContentError from "@components/layouts/ContentError";
import {Layout} from "antd";

const {Content, Header} = Layout;

export default function LayoutContent({children}) {

    const logError = (error, info) => {
        console.log({error, componentStack: info?.componentStack})
    }

    return <Content>
        <ErrorBoundary fallback={<ContentError/>} onError={logError}>
            {children}
        </ErrorBoundary>
    </Content>
}