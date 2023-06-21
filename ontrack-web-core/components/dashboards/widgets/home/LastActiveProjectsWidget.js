import Widget from "@components/dashboards/widgets/Widget";

export default function LastActiveProjectsWidget({count}) {
    return (
        <Widget title={`Last ${count} active projects`}>
            List of projects
        </Widget>
    )
}