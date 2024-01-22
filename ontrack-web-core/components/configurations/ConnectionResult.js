import {Alert, Space} from "antd";

export default function ConnectionResult({connectionResult}) {
    return (
        <>
            {
                connectionResult && <Space className="ot-line">
                    {
                        connectionResult.type === 'ERROR' &&
                        <Alert
                            type="error"
                            message={connectionResult.message}
                            closable
                            style={{
                                marginTop: 16,
                                padding: 16,
                            }}
                        />
                    }
                    {
                        connectionResult.type === 'OK' &&
                        <Alert
                            type="success"
                            message="Connection OK"
                            closable
                            style={{
                                marginTop: 16,
                                padding: 16,
                            }}
                        />
                    }
                </Space>
            }
        </>
    )
}