package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.model.IndicatorConstants
import net.nemerosa.ontrack.extension.indicators.portfolio.*
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Pattern

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
                    Text.of(IndicatorPortfolio::id.name)
                            .label("ID")
                            .regex(IndicatorConstants.INDICATOR_ID_PATTERN)
            )
            .with(
                    Text.of(IndicatorPortfolio::name.name)
                            .label("Name")
            )

    /**
     * Creating a portfolio
     */
    @PostMapping("create")
    fun createPortfolio(@RequestBody @Valid input: PortfolioCreationForm): ResponseEntity<String> {
        return ResponseEntity.ok(
                indicatorPortfolioService.createPortfolio(input.id, input.name).id
        )
    }

    /**
     * Updating a portfolio
     */
    @PutMapping("{id}/update")
    fun updatePortfolio(@PathVariable id: String, @RequestBody input: PortfolioUpdateForm): ResponseEntity<String> {
        val portfolio = indicatorPortfolioService.findPortfolioById(id)
        return if (portfolio == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(
                    indicatorPortfolioService.updatePortfolio(id, input).id
            )
        }
    }

    /**
     * Updating the global indicators
     */
    @PutMapping("global-indicators/update")
    fun updatePortfolioGlobalIndicators(@RequestBody input: PortfolioGlobalIndicators): ResponseEntity<IndicatorPortfolioOfPortfolios> =
            ResponseEntity.ok(
                    indicatorPortfolioService.savePortfolioOfPortfolios(input)
            )

    /**
     * Deleting a portfolio
     */
    @DeleteMapping("{id}/delete")
    fun deletePortfolio(@PathVariable id: String): ResponseEntity<Ack> {
        indicatorPortfolioService.deletePortfolio(id)
        return ResponseEntity.ok(Ack.OK)
    }

    class PortfolioCreationForm(
            @get:Pattern(regexp = IndicatorConstants.INDICATOR_ID_PATTERN)
            val id: String,
            val name: String
    )

}