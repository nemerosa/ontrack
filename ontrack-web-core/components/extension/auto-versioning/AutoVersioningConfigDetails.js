import {Descriptions, Typography} from "antd";
import AutoVersioningPostProcessing from "@components/extension/auto-versioning/AutoVersioningPostProcessing";
import AutoVersioningConfigNotifications from "@components/extension/auto-versioning/AutoVersioningConfigNotifications";
import AutoVersioningAdditionalPaths from "@components/extension/auto-versioning/AutoVersioningAdditionalPaths";

export default function AutoVersioningConfigDetails({source}) {

    const items = [
        {
            key: 'versionSource',
            label: 'Version source',
            children: <Typography.Text code>{source.versionSource}</Typography.Text>,
            span: 4,
        },
        {
            key: 'targetPropertyType',
            label: 'Target property type',
            children: <Typography.Text code>{source.targetPropertyType}</Typography.Text>,
            span: 4,
        },
        {
            key: 'targetProperty',
            label: 'Target property',
            children: <Typography.Text code>{source.targetProperty}</Typography.Text>,
            span: 4,
        },
        {
            key: 'targetPropertyRegex',
            label: 'Target property regex',
            children: <Typography.Text code>{source.targetPropertyRegex}</Typography.Text>,
            span: 4,
        },
        {
            key: 'targetRegex',
            label: 'Target regex',
            children: <Typography.Text code>{source.targetRegex}</Typography.Text>,
            span: 4,
        },
        {
            key: 'qualifier',
            label: 'Qualifier',
            children: <Typography.Text code>{source.qualifier}</Typography.Text>,
            span: 4,
        },
        {
            key: 'upgradeBranchPattern',
            label: 'Upgrade branch pattern',
            children: <Typography.Text code>{source.upgradeBranchPattern}</Typography.Text>,
            span: 4,
        },
        {
            key: 'validationStamp',
            label: 'Validation stamp',
            children: <Typography.Text code>{source.validationStamp}</Typography.Text>,
            span: 4,
        },
        {
            key: 'backValidation',
            label: 'Back validation',
            children: <Typography.Text code>{source.backValidation}</Typography.Text>,
            span: 4,
        },
        {
            key: 'prTitleTemplate',
            label: 'PR Title template',
            children: <Typography.Text code>{source.prTitleTemplate}</Typography.Text>,
            span: 12,
        },
        {
            key: 'prBodyTemplateFormat',
            label: 'PR Body template format',
            children: <Typography.Text code>{source.prBodyTemplateFormat}</Typography.Text>,
            span: 4,
        },
        {
            key: 'prBodyTemplate',
            label: 'PR Body template',
            children: <Typography.Text code>{source.prBodyTemplate}</Typography.Text>,
            span: 8,
        },
        {
            key: 'buildLinkCreation',
            label: 'Build link creation',
            children: <Typography.Text code>{source.buildLinkCreation}</Typography.Text>,
            span: 4,
        },
        {
            key: 'reviewers',
            label: 'Reviewers',
            children: <>
                {source.reviewers && source.reviewers.length > 0 &&
                    <ul>
                        {
                            source.reviewers.map(reviewer => (<>
                                <li key={reviewer}>{reviewer}</li>
                            </>))
                        }
                    </ul>
                }
            </>,
            span: 8,
        },
        {
            key: 'postProcessing',
            label: 'Post processing',
            children: <AutoVersioningPostProcessing
                type={source.postProcessing}
                config={source.postProcessingConfig}
            />,
            span: 12,
        },
        {
            key: 'notifications',
            label: 'Notifications',
            children: <AutoVersioningConfigNotifications
                notifications={source.notifications}
            />,
            span: 12,
        },
        {
            key: 'additionalPaths',
            label: "Additional paths",
            children: <AutoVersioningAdditionalPaths additionalPaths={source.additionalPaths}/>,
        },
    ]

    return (
        <>
            <Descriptions
                items={items}
                column={12}
                bordered={true}
            />
        </>
    )
}