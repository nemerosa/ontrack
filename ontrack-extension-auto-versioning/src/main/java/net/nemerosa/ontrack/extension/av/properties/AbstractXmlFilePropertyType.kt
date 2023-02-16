package net.nemerosa.ontrack.extension.av.properties

import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

abstract class AbstractXmlFilePropertyType : AbstractTextFilePropertyType() {

    override fun readProperty(content: String, targetProperty: String?): String? {
        val doc = readXml(content)
        return readProperty(doc, targetProperty)
    }

    private fun readXml(content: String): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val input = InputSource(StringReader(content))
        return builder.parse(input)
    }

    override fun replaceProperty(content: String, targetProperty: String?, targetVersion: String): String {
        val doc = readXml(content)
        replaceProperty(doc, targetProperty, targetVersion)
        val factory = TransformerFactory.newInstance()
        val transformer = factory.newTransformer()
        val source = DOMSource(doc)
        val writer = StringWriter()
        val output = StreamResult(writer)
        transformer.transform(source, output)
        return writer.toString()
    }

    abstract fun replaceProperty(doc: Document, targetProperty: String?, targetVersion: String)

    abstract fun readProperty(doc: Document, targetProperty: String?): String?
}