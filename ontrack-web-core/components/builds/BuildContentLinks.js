import PageSection from "@components/common/PageSection";
import BuildLinksGraph from "@components/links/BuildLinksGraph";

export default function BuildContentLinks({build}) {
    return (
        <>
            <PageSection title="Build links"
                         padding={false}
                         fullHeight={true}>
                <BuildLinksGraph
                    build={build}
                />
            </PageSection>
        </>
    )
}