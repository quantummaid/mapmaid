package de.quantummaid.mapmaid.specs

import de.quantummaid.mapmaid.MapMaid.aMapMaid
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given
import org.junit.jupiter.api.Test

class InheritanceSpecs {

    @Test
    fun typeCanBeRegisteredForInheritance() {
        given(
            aMapMaid()
                .withAdvancedSettings {  }
                .build()
        )
    }
}