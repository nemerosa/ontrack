import BuildLinksGraph from "@components/links/BuildLinksGraph";
import GridCell from "@components/grid/GridCell";

export default function BuildContentLinks({build}) {
    return (
        <>
            <GridCell title="Build links"
                      padding={false}>
                <BuildLinksGraph
                    build={build}
                />
            </GridCell>
        </>
    )
}