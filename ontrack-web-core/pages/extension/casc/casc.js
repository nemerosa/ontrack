import StandardPage from "@components/layouts/StandardPage";
import {CloseToHomeCommand} from "@components/common/Commands";
import Casc from "@components/extension/casc/Casc";
import CascDownloadJSONSchema from "@components/extension/casc/CascDownloadJSONSchema";

export default function CascPage() {
    return (
        <>
            <StandardPage
                pageTitle="Configuration as Code"
                commands={[
                    <CascDownloadJSONSchema key="schema-json"/>,
                    <CloseToHomeCommand key="home"/>,
                ]}
            >
                <Casc/>
            </StandardPage>
        </>
    )
}