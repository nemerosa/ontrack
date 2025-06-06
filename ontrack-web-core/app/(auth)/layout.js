/**
 * Root layout for the auth page
 */

import "./auth.css"

export default function RootLayout({children}) {
    return (
        <>
            <html lang="en">
            <head>
                <title>Yontrack - Signin</title>
            </head>
            <body>
            {children}
            </body>
            </html>
        </>
    )
}