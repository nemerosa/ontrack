import StandardPage from "@components/layouts/StandardPage";
import {CloseCommand} from "@components/common/Commands";
import {cascUri} from "@components/extension/casc/CascLinks";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import Link from "next/link";
import CascSchema from "@components/extension/casc/CascSchema";

export default function CascSchemaPage() {
    return (
        <>
            <StandardPage
                pageTitle="CasC Schema"
                breadcrumbs={[
                    ...homeBreadcrumbs(),
                    <Link key="casc" href={cascUri}>CasC</Link>,
                ]}
                commands={[
                    <CloseCommand key="close" href={cascUri}/>,
                ]}
            >
                <CascSchema/>
            </StandardPage>
        </>
    )
}