import DOMPurify from 'dompurify';

export default function SafeHTMLComponent({htmlContent}) {
    return (
        <span dangerouslySetInnerHTML={{__html: DOMPurify.sanitize(htmlContent)}}/>
    );
}
