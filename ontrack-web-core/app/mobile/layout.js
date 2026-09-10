/**
 * Root layout for the mobile UI.
 *
 * `/mobile` is a separate App Router root with its own <html> and <body>, like
 * `app/(auth)` - which means it is covered by neither `pages/_document.js` nor
 * the provider stack in `pages/_app.js`, and has to bring both itself.
 *
 * The theme tokens come from `styles/globals.css` rather than a mobile copy: the
 * two UIs are one product and must not drift into different colours. Only the
 * phone-specific layout lives in `mobile.css`.
 */

import "antd/dist/reset.css"
import "@/styles/globals.css"
import "./mobile.css"
import {themeInitScript} from "@components/theme/themeInitScript"
import MobileProviders from "./MobileProviders"
import MobileLayout from "@components/mobile/layout/MobileLayout"

export default function MobileRootLayout({children}) {
    return (
        <>
            {/*
              `suppressHydrationWarning` because this is an App Router root
              layout, so React hydrates <html> itself - and by then the inline
              script below has already stamped `data-theme` and `color-scheme` on
              it, which the client render does not reproduce.
            */}
            <html lang="en" suppressHydrationWarning>
            <head>
                <title>Yontrack</title>
                {/*
                  `viewport-fit=cover` lets the shell paint into the notch and the
                  home-indicator strip; `mobile.css` then keeps the header and the
                  bottom bar clear of both with `env(safe-area-inset-*)`.
                */}
                <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover"/>
                {/*
                  The colour behind the status bar, so it matches the header.
                  A literal, and the same one in both themes, because the header
                  is: `globals.css` fixes it to the brand purple either way, and
                  a `meta` tag cannot read a custom property.
                */}
                <meta name="theme-color" content="#3F3053"/>
                <link rel="shortcut icon" href="/favicon.ico"/>
                {/*
                  Decides the theme before the first paint, exactly as
                  `pages/_document.js` does for the desktop UI. Every colour in
                  `globals.css` keys off the `data-theme` it stamps.
                */}
                <script dangerouslySetInnerHTML={{__html: themeInitScript}}/>
            </head>
            <body>
            <MobileProviders>
                <MobileLayout>
                    {children}
                </MobileLayout>
            </MobileProviders>
            </body>
            </html>
        </>
    )
}
