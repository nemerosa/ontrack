package net.nemerosa.ontrack.repository

import com.fasterxml.jackson.databind.JsonNode

interface PreferencesRepository {

    fun getPreferences(accountId: Int): JsonNode?

    fun setPreferences(accountId: Int, json: JsonNode)

}