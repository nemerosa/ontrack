package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class IndicatorPortfolioIdAlreadyExistingException(id: String) : NotFoundException("Indicator portfolio with ID = $id already exists.")