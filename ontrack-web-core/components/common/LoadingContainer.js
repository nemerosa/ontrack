import {Spin} from "antd";

export default function LoadingContainer({loading, tip, children}) {
    return (
        <>
            {
                loading &&
                <div style={{
                    margin: '20px 0',
                    marginBottom: '20px',
                    padding: '30px 50px',
                    textAlign: 'center',
                    background: 'rgba(0, 0, 0, 0.05)',
                    borderRadius: '4px',
                }}>
                    <Spin tip={tip}/>
                </div>
            }
            {
                !loading && children
            }
        </>
    )
}