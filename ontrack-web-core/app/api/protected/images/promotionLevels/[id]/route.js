import {getImage, putImage} from "@/app/api/protected/images/images";

const uri = (params) => `rest/structure/promotionLevels/${params.id}/image`

export const GET = getImage({uri})
export const PUT = putImage({uri})
