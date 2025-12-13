import {getServerSession} from "next-auth";
import {authOptions} from "@/app/api/auth/authOptions";

export const backend = {
    url: process.env.ONTRACK_URL ?? "http://localhost:8080"
}

export const getAccessToken = async () => {
    const session = await getServerSession(authOptions)

    if (!session || !session.accessToken) {
        return null
    } else {
        return session.accessToken
    }
}
