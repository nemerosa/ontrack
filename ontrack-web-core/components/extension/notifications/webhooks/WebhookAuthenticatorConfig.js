import {Dynamic} from "@components/common/Dynamic";

export default function WebhookAuthenticatorConfig({authenticationType, prefix, creation}) {
    return (
        <>
            <Dynamic
                path={`framework/webhook-authenticator/${authenticationType}/Form.js`}
                props={{prefix, creation}}
            />
        </>
    )
}