package net.nemerosa.ontrack.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValues
import java.io.StringWriter

class Yaml {

    private val yamlFactory = YAMLFactory().apply {
        enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE)
    }

    private val mapper = ObjectMapper(yamlFactory).apply {
        registerModule(KotlinModule.Builder().build())
    }

    /**
     * Reads some Yaml as a list of documents
     */
    fun read(content: String): List<ObjectNode> {
        val parser = yamlFactory.createParser(content)
        return mapper
            .readValues<ObjectNode>(parser)
            .readAll()
    }

    fun write(json: List<ObjectNode>): String {
        val writer = StringWriter()
        json.forEach {
            val generator = yamlFactory.createGenerator(writer)
            generator.writeObject(it)
            writer.append('\n')
        }
        return writer.toString()
    }

}