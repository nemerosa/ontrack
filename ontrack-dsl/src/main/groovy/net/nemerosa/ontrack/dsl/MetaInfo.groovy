package net.nemerosa.ontrack.dsl

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class MetaInfo {

    final String name
    final String value
    final String link

    MetaInfo(String name, String value, String link) {
        this.name = name
        this.value = value
        this.link = link
    }

    MetaInfo(Object node) {
        this.name = node['name']
        this.value = node['value']
        this.link = node['link']
    }

    Map<String, String> getMap() {
        [
                name : name,
                value: value,
                link : link,
        ]
    }

}
