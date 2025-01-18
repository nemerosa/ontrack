import StandardPage from "@components/layouts/StandardPage";
import {CloseToHomeCommand, Command} from "@components/common/Commands";
import {FaSitemap} from "react-icons/fa";
import {cascSchemaUri} from "@components/extension/casc/CascLinks";
import Casc from "@components/extension/casc/Casc";

export default function CascPage() {
    return (
        <>
            <StandardPage
                pageTitle="Configuration as Code"
                commands={[
                    <Command key="schema" icon={<FaSitemap/>} href={cascSchemaUri} text="Schema"/>,
                    <CloseToHomeCommand key="home"/>,
                ]}
            >
                <Casc/>
            </StandardPage>
        </>
    )
}