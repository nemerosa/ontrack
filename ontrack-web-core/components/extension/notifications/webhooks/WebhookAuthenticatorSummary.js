import {Dynamic} from "@components/common/Dynamic";

export default function WebhookAuthenticatorSummary({authenticationType}) {
    return (
        <>
            <Dynamic
                path={`framework/webhook-authenticator/${authenticationType}/Summary.js`}
                props={{}}
            />
        </>
    )
}