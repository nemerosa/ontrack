package net.nemerosa.ontrack.dsl.v4

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString
class MetaInfo {

    final String name
    final String value
    final String link
    final String category

    MetaInfo(String name, String value, String link, String category) {
        this.name = name
        this.value = value
        this.link = link
        this.category = category
    }

    MetaInfo(Object node) {
        this.name = node['name']
        this.value = node['value']
        this.link = node['link']
        this.category = node['category']
    }

    Map<String, String> getMap() {
        [
                name    : name,
                value   : value,
                link    : link,
                category: category,
        ]
    }

}
