package net.nemerosa.ontrack.extension.chart

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class ChartProviderNotFoundException(name: String) : NotFoundException(
    "Chart provider with name [$name] cannot be found."
)
