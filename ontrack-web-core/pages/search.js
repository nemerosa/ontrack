import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import SearchView from "@components/search/SearchView";

export const useSearch = () => {

    const router = useRouter()

    return async (value) => {
        await router.push(`/search?q=${encodeURIComponent(value)}`)
    }

}

export default function SearchPage() {

    const router = useRouter()

    return (
        <>
            <main>
                <MainLayout>
                    <SearchView key={router.asPath}/>
                </MainLayout>
            </main>
        </>
    )
}