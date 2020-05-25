package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class IndicatorPortfolioNotFoundException(id: String) : NotFoundException("Indicator portfolio with ID = $id was not found.")