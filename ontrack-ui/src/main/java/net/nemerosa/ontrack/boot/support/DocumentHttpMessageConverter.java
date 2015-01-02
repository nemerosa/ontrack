package net.nemerosa.ontrack.boot.support;

import net.nemerosa.ontrack.model.structure.Document;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

public class DocumentHttpMessageConverter extends AbstractHttpMessageConverter<Document> {

    public DocumentHttpMessageConverter() {
        super(MediaType.IMAGE_GIF, MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Document.class.isAssignableFrom(clazz);
    }

    @Override
    protected Document readInternal(Class<? extends Document> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return new Document(
                inputMessage.getHeaders().getContentType().toString(),
                IOUtils.toByteArray(inputMessage.getBody())
        );
    }

    @Override
    protected void writeInternal(Document document, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        outputMessage.getHeaders().setContentType(MediaType.parseMediaType(document.getType()));
        byte[] content = document.getContent();
        outputMessage.getHeaders().setContentLength(content.length);
        outputMessage.getBody().write(content);
    }
}
