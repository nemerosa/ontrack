import GraphQLEnumSelect from "@components/common/select/GraphQLEnumSelect";
import {gql} from "graphql-request";

export default function SelectBuildGitCommitLink({id, value, onChange}) {
    return (
        <>
            <GraphQLEnumSelect
                id={id}
                value={value}
                onChange={onChange}
                query={
                    gql`
                        query SelectBuildGitCommitLink {
                            buildGitCommitLinks {
                                id
                                name
                            }
                        }
                    `
                }
                queryNode="buildGitCommitLinks"
                entryValue={entry => entry.id}
                entryLabel={entry => entry.name}
                width="20em"
            />
        </>
    )
}