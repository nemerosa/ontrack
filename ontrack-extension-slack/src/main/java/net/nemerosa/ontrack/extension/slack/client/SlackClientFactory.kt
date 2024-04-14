package net.nemerosa.ontrack.extension.slack.client

interface SlackClientFactory {

    fun getSlackClient(slackToken: String, endpointUrl: String?): SlackClient
    
}