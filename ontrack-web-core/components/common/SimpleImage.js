import Image from "next/image";
import {useRouter} from "next/router";

export default function SimpleImage({src, size, alt}) {
    const router = useRouter()
    return (
        <Image alt={alt} width={size} height={size} src={`${router.basePath}${src}`}/>
    )
}