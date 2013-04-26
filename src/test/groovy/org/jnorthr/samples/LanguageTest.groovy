package org.jnorthr.samples;

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.*
import static org.junit.matchers.JUnitMatchers.*
import org.junit.Test

class LanguageTest {
    def languages = [tom:['Java', 'Groovy'], dick:['C#', 'Ruby']]

    @Test void tomKnowsJavaAndGroovyHamcrest() {
        assertThat languages['tom'], is(["Java", "Groovy"])
        assertThat languages['tom'][0], containsString("v")
        assertThat languages['tom'][1], containsString("v")
    }

    @Test void tomKnowsJavaAndGroovyNative() {
        assert languages['tom'] == ["Java", "Groovy"]
        assert languages['tom'].every{ it.contains("v") }
    }
}