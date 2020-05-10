package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioService
import net.nemerosa.ontrack.extension.indicators.portfolio.PortfolioUpdateForm
import net.nemerosa.ontrack.model.Ack
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
     * Deleting a portfolio
     */
    @DeleteMapping("{id}/delete")
    fun deletePortfolio(@PathVariable id: String): ResponseEntity<Ack> {
        indicatorPortfolioService.deletePortfolio(id)
        return ResponseEntity.ok(Ack.OK)
    }

    class PortfolioCreationForm(
            val name: String
    )

}