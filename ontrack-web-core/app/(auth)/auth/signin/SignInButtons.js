"use client";

import {signIn} from "next-auth/react";

export default function SignInButtons({providers}) {
    if (!providers) return null;

    const providerSignIn = (provider) => {
        return async () => {
            await signIn(provider.id, {callbackUrl: "/"})
        }
    }

    return (
        <div className="button-container">
            {Object.values(providers).map((provider) => (
                <button
                    key={provider.name}
                    onClick={providerSignIn(provider)}
                    className="signin-button"
                >
                    Sign in with {provider.name}
                </button>
            ))}
        </div>
    );
}
