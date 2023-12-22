import Link from "next/link";

export default function GitHubUser({user}) {
    return (
        <>
            {
                user &&
                <Link href={user.url}>
                    {user.login}
                </Link>
            }
        </>
    )
}