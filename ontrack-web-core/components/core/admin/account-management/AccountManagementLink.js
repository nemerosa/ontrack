import Link from "next/link";

export const accountManagementUri = '/core/admin/account-management'

export default function AccountManagementLink() {
    return (
        <>
            <Link href={accountManagementUri}>
                Account management
            </Link>
        </>
    )
}