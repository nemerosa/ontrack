import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import LoadingContainer from "@components/common/LoadingContainer";
import {List} from "antd";

export default function CascLocations() {

    const {data, loading} = useQuery(
        gql`
            query CasC {
                casc {
                    locations
                }
            }
        `
    )

    return (
        <>
            <LoadingContainer loading={loading}>
                {
                    data &&
                    <List
                        dataSource={data.casc.locations}
                        renderItem={(item) => (
                            <List.Item>
                                <List.Item.Meta
                                    title={item}
                                />
                            </List.Item>
                        )}
                    />
                }
            </LoadingContainer>
        </>
    )
}