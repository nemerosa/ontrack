package net.nemerosa.ontrack.extension.indicators.imports

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/extension/indicators/imports")
class IndicatorImportsController(
        private val importsService: IndicatorImportsService
) {

    @PostMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun imports(@RequestBody @Valid data: IndicatorImports) {
        importsService.imports(data)
    }

}