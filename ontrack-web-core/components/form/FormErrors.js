import {Alert, Space} from "antd";
import {Fragment} from "react";

export default function FormErrors({errors}) {
    return (
        errors
            ? <Space
                direction="vertical"
                style={{
                    width: '100%',
                }}>
                {
                    errors.map((error, index) => (
                        <Fragment key={`form-error-${index}`}>
                            <Alert
                                type="error"
                                message={error}
                                closable
                                style={{
                                    marginTop: 16,
                                    padding: 16,
                                }}
                            />
                        </Fragment>
                    ))
                }
            </Space>
            : <></>
    )
}