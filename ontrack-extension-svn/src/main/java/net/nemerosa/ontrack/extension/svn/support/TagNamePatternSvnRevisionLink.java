package net.nemerosa.ontrack.extension.svn.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.scm.support.TagPattern;
import net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLink;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.exceptions.JsonParsingException;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Build / revision relationship based on the build name being a subversion tag name which must follow a given pattern.
 */
@Component
public class TagNamePatternSvnRevisionLink implements BuildSvnRevisionLink<TagPattern> {

    @Override
    public String getId() {
        return "tagPattern";
    }

    @Override
    public String getName() {
        return "Tag pattern as name";
    }

    @Override
    public TagPattern clone(TagPattern data, Function<String, String> replacementFunction) {
        return data.clone(replacementFunction);
    }

    @Override
    public TagPattern parseData(JsonNode node) {
        try {
            return ObjectMapperFactory.create().treeToValue(node, TagPattern.class);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(e);
        }
    }

    @Override
    public JsonNode toJson(TagPattern data) {
        return ObjectMapperFactory.create().valueToTree(data);
    }

    @Override
    public Form getForm() {
        return Form.create()
                .with(
                        Text.of("pattern")
                                .label("Tag pattern")
                                .help("@file:extension/scm/help.net.nemerosa.ontrack.extension.svn.support.TagNamePatternSvnRevisionLink.tagPattern.tpl.html")
                )
                ;
    }
}
