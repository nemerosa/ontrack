export default function ColorBox({color}) {
    return (
        <>
            <span
                className="ot-box"
                style={{
                    backgroundColor: color,
                }}
            ></span>
        </>
    )
}