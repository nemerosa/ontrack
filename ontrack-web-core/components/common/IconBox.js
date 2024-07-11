export default function IconBox({bgColor, fgColor, icon}) {
    return (
        <>
            <div
                style={{
                    width: '2em',
                    height: '2em',
                    display: 'inline-block',
                    alignContent: "center",
                    textAlign: "center",
                    backgroundColor: bgColor,
                    color: fgColor,
                    borderRadius: '15%',
                }}
            >
                {icon}
            </div>
        </>
    )
}