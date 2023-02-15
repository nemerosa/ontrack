package net.nemerosa.ontrack.extension.av.properties

import org.w3c.dom.Document
import org.w3c.dom.NodeList
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

abstract class AbstractXPathFilePropertyType : AbstractXmlFilePropertyType() {

    protected fun getValueByXPath(doc: Document, path: String): String? {
        val xPath: XPath = XPathFactory.newInstance().newXPath()
        val nodeList = xPath.compile(path).evaluate(doc, XPathConstants.NODESET) as NodeList
        return if (nodeList.length > 0) {
            val node = nodeList.item(0)
            node.textContent
        } else {
            null
        }
    }

    protected fun setValueByXPath(doc: Document, path: String, value: String) {
        val xPath: XPath = XPathFactory.newInstance().newXPath()
        val nodeList = xPath.compile(path).evaluate(doc, XPathConstants.NODESET) as NodeList
        if (nodeList.length > 0) {
            val node = nodeList.item(0)
            node.textContent = value
        }
    }

}