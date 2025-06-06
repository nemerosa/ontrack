import {getProviders} from "next-auth/react";
import SignInButtons from "./SignInButtons";
import Image from "next/image";

export default async function SignInPage() {
    const providers = await getProviders();

    return (
        <div style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            minHeight: "75vh",
            justifyContent: "center"
        }}>
            <div style={{
                marginBottom: "64px",
            }}>
                <Image
                    src={`/yontrack-logo.svg`}
                    alt={"Yontrack logo"}
                    width={480}
                    height={64}
                />
            </div>
            <SignInButtons providers={providers}/>
        </div>
    );
}
