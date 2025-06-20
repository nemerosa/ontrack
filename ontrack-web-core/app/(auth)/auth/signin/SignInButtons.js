"use client";

import {getProviders, signIn} from "next-auth/react";
import {useEffect, useState} from "react";
import {Spin} from "antd";

export default function SignInButtons() {

    const [providers, setProviders] = useState({})
    useEffect(() => {
        const loadProviders = async (attempt = 1) => {
            const data = await getProviders()
            if (data || attempt > 3) {
                setProviders(data || {})
            } else {
                setTimeout(() => loadProviders(attempt + 1), 500); // retry
            }
        };
        loadProviders()
    }, [])

    const providerSignIn = (provider) => {
        return async () => {
            await signIn(provider.id, {callbackUrl: "/"})
        }
    }

    return (
        <div className="button-container">
            {
                !providers &&
                <Spin/>
            }
            {
                providers && <>
                    {
                        Object.values(providers).map((provider) => (
                            <button
                                key={provider.name}
                                onClick={providerSignIn(provider)}
                                className="signin-button"
                            >
                                Sign in with {provider.name}
                            </button>
                        ))
                    }
                </>
            }
        </div>
    );
}
