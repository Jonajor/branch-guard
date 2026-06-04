package com.github.jonajor.branchguard.branch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BranchNameGeneratorTest {
    @Test
    fun `renders default feature branch name`() {
        val result = BranchNameGenerator.generate(
            BranchNameInput(
                ticket = "ABC-123",
                description = "Fix Login Error",
                prefix = "feature",
                template = "{prefix}/{ticket}-{description}",
                ticketRegex = "[A-Z]+-\\d+",
            ),
        )

        assertTrue(result.isTicketValid)
        assertEquals("fix-login-error", result.sanitizedDescription)
        assertEquals("feature/ABC-123-fix-login-error", result.branchName)
    }

    @Test
    fun `validates ticket with configured regex`() {
        assertTrue(BranchNameGenerator.isTicketValid("ABC-123", "[A-Z]+-\\d+"))
        assertFalse(BranchNameGenerator.isTicketValid("abc", "[A-Z]+-\\d+"))
    }

    @Test
    fun `sanitizes invalid description characters`() {
        assertEquals(
            "fix-login-error",
            BranchNameGenerator.sanitizeDescription(" Fix Login: Error? "),
        )
    }

    @Test
    fun `renders custom template`() {
        val result = BranchNameGenerator.generate(
            BranchNameInput(
                ticket = "BUG-7",
                description = "Null check",
                prefix = "bugfix",
                template = "{prefix}/{ticket}/{description}",
                ticketRegex = "[A-Z]+-\\d+",
            ),
        )

        assertEquals("bugfix/BUG-7/null-check", result.branchName)
    }
}
