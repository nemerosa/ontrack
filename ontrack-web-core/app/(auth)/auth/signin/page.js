import SignInButtons from "./SignInButtons";
import Image from "next/image";

export default async function SignInPage() {
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
            <SignInButtons/>
        </div>
    );
}
