import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import GlobalMessage from "@components/layouts/GlobalMessage";

export default function MainGlobalMessages() {

    const client = useGraphQLClient()
    const [messages, setMessages] = useState([])

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query GlobalMessages {
                        globalMessages {
                            type
                            content
                        }
                    }
                `
            ).then(data => {
                setMessages(data.globalMessages)
            })
        }
    }, [client]);

    return (
        <>
            {
                messages && messages.length > 0 &&
                messages.map(({type, content}, index) => (
                        <GlobalMessage key={index} type={type} content={content}/>
                    )
                )
            }
        </>
    )
}