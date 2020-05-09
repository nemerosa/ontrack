package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioService
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/extension/indicators/portfolios")
class IndicatorPortfolioController(
        private val indicatorPortfolioService: IndicatorPortfolioService
) {

    /**
     * Form to create a portfolio
     */
    @GetMapping("create")
    fun getPortfolioCreationForm(): Form = Form.create()
            .with(
                    Text.of("name")
                            .label("Name")
            )

    /**
     * Creating a portfolio
     */
    @PostMapping("create")
    fun createPortfolio(@RequestBody input: PortfolioCreationForm): ResponseEntity<String> {
        return ResponseEntity.ok(
                indicatorPortfolioService.createPortfolio(input.name).id
        )
    }

    class PortfolioCreationForm(
            val name: String
    )

}