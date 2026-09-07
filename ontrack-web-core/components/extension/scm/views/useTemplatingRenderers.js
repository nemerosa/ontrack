import {gql} from "graphql-request";
import {useQuery} from "@components/services/GraphQL";

/**
 * The renderers the server can render a change log with — `text`, `markdown`, `html`, `jira`,
 * `slack`.
 *
 * `@components/extension/issues/SelectTemplateRenderer` exports a hook of the same shape, but
 * it is built on the deprecated `useGraphQLClient`; migrating that file is its own task, and
 * no new usage of that hook may be introduced.
 */
export default function useTemplatingRenderers() {
    const {data} = useQuery(
        gql`
            query TemplatingRenderers {
                templatingRenderers {
                    id
                    name
                }
            }
        `,
        {
            initialData: [],
            dataFn: data => data.templatingRenderers,
        }
    )
    return data ?? []
}
