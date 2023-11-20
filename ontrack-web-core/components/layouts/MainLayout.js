import {Layout} from "antd";
import NavBar from "@components/layouts/NavBar";
import {createContext, useContext, useEffect, useState} from "react";
import MainLayoutRestoreViewButton from "@components/layouts/MainLayoutRestoreViewButton";
import {useRouter} from "next/router";

const {Content, Header} = Layout;

export const MainLayoutContext = createContext({expanded: false})

export default function MainLayout({children}) {

    const [expanded, setExpanded] = useState(false)

    const router = useRouter()

    useEffect(() => {
        // Gets the `expanded` query parameter
        setExpanded(router.query.expanded)
    }, [router])

    const toggleExpansion = () => {
        setExpanded(!expanded)
    }

    return (
        <>
            <MainLayoutContext.Provider value={{expanded, toggleExpansion}}>
                {
                    !expanded && <Layout>
                        <Header>
                            <NavBar/>
                        </Header>
                        <Content>
                            {children}
                        </Content>
                    </Layout>
                }
                {
                    expanded && <Layout>
                        <Content>
                            {children}
                        </Content>
                    </Layout>
                }
                <MainLayoutRestoreViewButton/>
            </MainLayoutContext.Provider>
        </>
    )
}