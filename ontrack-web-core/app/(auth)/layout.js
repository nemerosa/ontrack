/**
 * Root layout for the auth page
 */

import "./auth.css"

export default function RootLayout({children}) {
    return (
        <>
            <html lang="en">
            <body>
            {children}
            </body>
            </html>
        </>
    )
}