export default function SonarqubeIcon({size = 16, ...props}) {
    return (
        <>
            <svg xmlns="http://www.w3.org/2000/svg"
                 viewBox="0 0 64 64"
                 width={size}
                 height={size}
                 fill="currentColor"
                 {...props}
            >
                <circle cx="32" cy="32" r="30" fill="none" stroke="currentColor" strokeWidth="4"/>
                <path d="M12 40c8-12 32-12 40 0" stroke="currentColor" strokeWidth="4" fill="none"
                      strokeLinecap="round"/>
                <path d="M16 48c6-8 26-8 32 0" stroke="currentColor" strokeWidth="3" fill="none"
                      strokeLinecap="round"/>
            </svg>
        </>
    )
}