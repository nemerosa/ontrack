import {ErrorBoundary} from "react-error-boundary";
import ContentError from "@components/layouts/ContentError";
import {Layout} from "antd";

const {Content, Header} = Layout;

export default function LayoutContent({children}) {

    const logError = (error) => {
        console.log(error)
    }

    return <Content>
        <ErrorBoundary fallback={<ContentError/>} onError={logError}>
            {children}
        </ErrorBoundary>
    </Content>
}