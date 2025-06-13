import {getImage, putImage} from "@/app/api/protected/images/images";

const uri = (params) => `rest/admin/predefinedPromotionLevels/${params.id}/image`

export const GET = getImage({uri})
export const PUT = putImage({uri})
