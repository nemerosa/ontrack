import {createContext, useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";

export const JobCategoriesContext = createContext({
    categories: [],
    selectedCategory: '',
    setSelectedCategory: () => {
    },
})

export default function JobCategoriesContextProvider({children}) {

    const client = useGraphQLClient()
    const [categories, setCategories] = useState([])
    const [selectedCategory, setSelectedCategory] = useState('')

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query JobCategories {
                        jobCategories {
                            key
                            name
                            types {
                                key
                                name
                            }
                        }
                    }
                `
            ).then(data => {
                setCategories(data.jobCategories)
            })
        }
    }, [client]);

    const context = {
        categories,
        selectedCategory,
        setSelectedCategory,
    }

    return (
        <JobCategoriesContext.Provider value={context}>
            {children}
        </JobCategoriesContext.Provider>
    )
}