import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Space, Spin, Typography, Upload} from "antd";
import {FaPlus} from "react-icons/fa";
import {useGraphQLClient, useRestClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {useContext, useEffect, useState} from "react";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";
import FormErrors from "@components/form/FormErrors";
import {EventsContext} from "@components/common/EventsContext";

const getBase64 = (img, dataUrl, callback) => {
    const reader = new FileReader()
    reader.addEventListener('load', () => {
        const result = reader.result
        if (dataUrl) {
            return callback(result)
        } else {
            const text = btoa(result)
            return callback(text)
        }
    })
    if (dataUrl) {
        reader.readAsDataURL(img)
    } else {
        reader.readAsBinaryString(img)
    }
};

export const usePromotionLevelChangeImageDialog = () => {

    const client = useGraphQLClient()
    const restClient = useRestClient()

    const [promotionLevel, setPromotionLevel] = useState()
    const [image, setImage] = useState()

    const eventsContext = useContext(EventsContext)

    return useFormDialog({
        init: (form, {id}) => {
            client.request(
                gql`
                    query PromotionLevel($id: Int!) {
                        promotionLevel(id: $id) {
                            id
                            name
                            image
                        }
                    }
                `,
                {id}
            ).then(data => {
                setPromotionLevel(data.promotionLevel)
            })
        },
        onSuccess: (values, {id}) => {
            const file = image
            if (file?.status === 'done') {
                getBase64(file.originFileObj, false, (data) => {
                    restClient.put(`/rest/structure/promotionLevels/${id}/image`, data).then(() => {
                        eventsContext.fireEvent("promotionLevel.image", {id})
                    })
                })
            }
        },
        promotionLevel,
        setImage,
    })
}

export default function PromotionLevelChangeImageDialog({promotionLevelChangeImageDialog}) {

    const [loading, setLoading] = useState(false)
    const [imageUrl, setImageUrl] = useState()
    const [fileSize, setFileSize] = useState('')
    const [fileType, setFileType] = useState('')

    const [submittable, setSubmittable] = useState(false)

    const onChange = (info) => {
        if (info.file.status === 'uploading') {
            setLoading(true)
        } else if (info.file.status === 'done') {
            const size = info.file.size
            const type = info.file.type

            setFileType(type)
            setFileSize(size)

            // Get this url from response in real world.
            getBase64(info.file.originFileObj, true, (url) => {
                setLoading(false)
                setImageUrl(url)
            })

            // Setting the field value
            promotionLevelChangeImageDialog.setImage(info.file)
        }
    }

    const formatSize = () => {
        if (fileSize) {
            if (fileSize < 1024) {
                return `${fileSize} bytes`
            } else {
                return `${Math.floor(fileSize / 1024)} KB`
            }
        } else {
            return ''
        }
    }

    const [formErrors, setFormErrors] = useState([])

    useEffect(() => {
        const errors = []
        if (fileSize && fileSize >= 16 * 1024) {
            errors.push("File exceeds 16 KB.")
        }
        if (fileType && fileType !== 'image/png') {
            errors.push("Only PNG files are supported.")
        }
        setSubmittable(fileSize && fileType && errors.length === 0)
        setFormErrors(errors)
    }, [fileSize, fileType]);

    return (
        <>
            <FormDialog dialog={promotionLevelChangeImageDialog} submittable={submittable}>
                <Form.Item
                    name="image"
                    label={
                        <Space>
                            {
                                promotionLevelChangeImageDialog.promotionLevel &&
                                <PromotionLevelImage promotionLevel={promotionLevelChangeImageDialog.promotionLevel}/>
                            }
                            Image
                        </Space>
                    }
                >
                    <Space className="ot-line">
                        <Upload
                            listType="picture-card"
                            maxCount={1}
                            showUploadList={false}
                            onChange={onChange}
                            style={{width: '100%'}}
                        >
                            <Space>
                                {
                                    imageUrl ?
                                        (
                                            <img
                                                src={imageUrl}
                                                alt="Image"
                                                style={{
                                                    width: '100%',
                                                }}
                                            />
                                        ) :
                                        <Space direction="vertical">
                                            {
                                                loading ? <Spin size="small"/> : <FaPlus/>
                                            }
                                            Upload
                                        </Space>
                                }
                            </Space>
                        </Upload>
                        <Space direction="vertical">
                            {
                                fileSize &&
                                <Typography.Text>File size: {formatSize()}</Typography.Text>
                            }
                            {
                                fileType &&
                                <Typography.Text>File type: {fileType}</Typography.Text>
                            }
                        </Space>
                    </Space>
                </Form.Item>
                <Form.Item>
                    <FormErrors errors={formErrors}/>
                </Form.Item>
            </FormDialog>
        </>
    )
}