export default function SafeHTMLComponent({htmlContent}) {
    return (
        <div dangerouslySetInnerHTML={{__html: htmlContent}}/>
    );
}
