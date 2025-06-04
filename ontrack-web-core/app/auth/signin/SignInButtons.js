"use client";

import {signIn} from "next-auth/react";
import {Button, Space} from "antd";

export default function SignInButtons({providers}) {
    if (!providers) return null;

    const providerSignIn = (provider) => {
        return async () => {
            await signIn(provider.id, {callbackUrl: "/"})
        }
    }

    return (
        <Space direction="vertical" size="middle">
            {Object.values(providers).map((provider) => (
                <Button
                    key={provider.name}
                    type="primary"
                    size="large"
                    onClick={providerSignIn(provider)}
                >
                    Sign in with {provider.name}
                </Button>
            ))}
        </Space>
    );
}
