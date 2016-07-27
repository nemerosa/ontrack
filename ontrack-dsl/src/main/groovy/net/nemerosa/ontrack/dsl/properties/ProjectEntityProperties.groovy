package net.nemerosa.ontrack.dsl.properties

import net.nemerosa.ontrack.dsl.MetaInfo
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.ProjectEntity
import net.nemerosa.ontrack.dsl.PropertyNotFoundException
import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod

@DSL
class ProjectEntityProperties {

    protected final Ontrack ontrack
    private final ProjectEntity entity

    ProjectEntityProperties(Ontrack ontrack, ProjectEntity entity) {
        this.ontrack = ontrack
        this.entity = entity
    }

    def property(String type, data) {
        entity.property(type, data)
    }

    def property(String type) {
        entity.property(type)
    }

    /**
     * Links
     */
    @DSLMethod
    def links(Map<String, String> links) {
        property('net.nemerosa.ontrack.extension.general.LinkPropertyType', [
                links: links.collect { k, v ->
                    [
                            name : k,
                            value: v,
                    ]
                }
        ])
    }

    @DSLMethod(see = "links")
    Map<String, String> getLinks() {
        property('net.nemerosa.ontrack.extension.general.LinkPropertyType').links.collectEntries {
            [it.name, it.value]
        }
    }

    /**
     * Message
     */

    def message(String text, String type = 'INFO') {
        property('net.nemerosa.ontrack.extension.general.MessagePropertyType', [
                type: type,
                text: text,
        ])
    }

    def getMessage() {
        property('net.nemerosa.ontrack.extension.general.MessagePropertyType')
    }

    /**
     * Meta info properties
     */

    def metaInfo(Map<String, String> map) {
        property('net.nemerosa.ontrack.extension.general.MetaInfoPropertyType', [
                items: map.collect { name, value ->
                    [
                            name : name,
                            value: value,
                    ]
                }
        ])
    }



    def metaInfo(String name, String value, String link = null, String category = null) {
        // Gets the list of meta info properties
        def items = metaInfo
        // Index by name
        Map<String, MetaInfo> map = items.collectEntries { item -> [item.name, item] }
        // Updates or sets the entry
        map[name] = new MetaInfo(name, value, link, category)
        // Edits the property
        property('net.nemerosa.ontrack.extension.general.MetaInfoPropertyType', [
                items: map.collect { itemName, item -> item.map }
        ])
    }

    List<MetaInfo> getMetaInfo() {
        try {
            return property('net.nemerosa.ontrack.extension.general.MetaInfoPropertyType').items.collect {
                new MetaInfo(it)
            }
        } catch (PropertyNotFoundException ignored) {
            return []
        }
    }

    /**
     * Jenkins job
     */

    @DSLMethod
    def jenkinsJob(String configuration, String job) {
        property('net.nemerosa.ontrack.extension.jenkins.JenkinsJobPropertyType', [
                configuration: configuration,
                job          : job,
        ])
    }

    @DSLMethod(see = "jenkinsJob")
    def getJenkinsJob() {
        property('net.nemerosa.ontrack.extension.jenkins.JenkinsJobPropertyType')
    }

    /**
     * Jenkins build
     */

    @DSLMethod
    def jenkinsBuild(String configuration, String job, int build) {
        property('net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType', [
                configuration: configuration,
                job          : job,
                build        : build,
        ])
    }

    @DSLMethod(see = "jenkinsBuild")
    def getJenkinsBuild() {
        property('net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType')
    }

}
