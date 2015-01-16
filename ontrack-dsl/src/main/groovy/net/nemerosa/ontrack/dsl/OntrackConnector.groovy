package net.nemerosa.ontrack.dsl

interface OntrackConnector {

    def get(String url)

    def post(String url, data)

    def put(String url, data)

    def upload(String url, String name, Object o)

    def upload(String url, String name, Object o, String contentType)

    Document download(String url)
}