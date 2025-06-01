import Image from "next/image";

export default function LoadingLogo() {
    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: '100vh',
        }}>
            <Image
                src={`/yontrack-logo.svg`}
                alt={"Yontrack logo"}
                width={480}
                height={64}
                className="logo-spin"
            />
        </div>
    )
}