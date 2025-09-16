import {Form, Input, InputNumber, Select, Switch} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.Item
                label="Configuration"
                extra="Name of the SonarQube configuration in Yontrack"
                name={prefixedFormName(prefix, ['configuration', 'name'])}
            >
                <Input/>
            </Form.Item>

            <Form.Item
                label="Project key"
                extra="Key of the project in SonarQube"
                name={prefixedFormName(prefix, 'key')}
            >
                <Input/>
            </Form.Item>

            <Form.Item
                label="Validation stamp"
                extra="Name of the validation stamp to monitor to trigger the collection of SQ measures"
                name={prefixedFormName(prefix, 'validationStamp')}
            >
                <Input/>
            </Form.Item>

            <Form.Item
                label="Measures"
                extra="List of measures to collect from SonarQube. If defined, they are added to the list of default measures defined in the settings or they can override the default ones."
                name={prefixedFormName(prefix, 'measures')}
            >
                <Select
                    mode="tags"
                    style={{width: '100%'}}
                />
            </Form.Item>

            <Form.Item
                label="Override"
                extra="If measures are set in the property, do they complete the default ones or override them?"
                name={prefixedFormName(prefix, 'override')}
            >
                <Switch/>
            </Form.Item>

            <Form.Item
                label="Branch model"
                extra="Use the project branch model (if any) to filter the branches where to collect the SonarQube measures."
                name={prefixedFormName(prefix, 'branchModel')}
            >
                <Switch/>
            </Form.Item>

            <Form.Item
                label="Branch pattern"
                extra="Regular expression to filter the branch where to collect the SonarQube measures."
                name={prefixedFormName(prefix, 'branchPattern')}
            >
                <Input/>
            </Form.Item>

            <Form.Item
                label="Metrics"
                extra="If checked, collected SQ measures will be attached as metrics to the validation."
                name={prefixedFormName(prefix, 'validationMetrics')}
            >
                <Switch/>
            </Form.Item>
        </>
    )
}