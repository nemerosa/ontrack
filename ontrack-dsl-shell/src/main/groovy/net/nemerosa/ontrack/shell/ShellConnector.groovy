package net.nemerosa.ontrack.shell

class ShellConnector {

    private final Map<String, Object> values = [:]

    def put(String name, Object value) {
        values.put(name, value)
    }

    Map<String, ?> getValues() {
        values
    }

}
