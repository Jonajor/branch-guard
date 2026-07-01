package com.github.jonajor.branchguard.branch

data class BranchNameResult(
    val branchName: String,
    val sanitizedDescription: String,
    val isTicketValid: Boolean,
)

object BranchNameGenerator {
    private val invalidGitCharacters = Regex("""[\s~^:?*\[\]\\]+""")
    private val repeatedSeparators = Regex("""[-/]{2,}""")

    fun generate(input: BranchNameInput): BranchNameResult {
        val ticket = input.ticket.trim().uppercase()
        val sanitizedDescription = sanitizeDescription(input.description)
        val prefix = sanitizePathPart(input.prefix.ifBlank { "feature" })
        val branchName = input.template.ifBlank { "{prefix}/{ticket}-{description}" }
            .replace("{prefix}", prefix)
            .replace("{ticket}", ticket)
            .replace("{description}", sanitizedDescription)
            .let(::sanitizeBranchName)

        return BranchNameResult(
            branchName = branchName,
            sanitizedDescription = sanitizedDescription,
            isTicketValid = isTicketValid(ticket, input.ticketRegex),
        )
    }

    fun isTicketValid(ticket: String, ticketRegex: String): Boolean {
        val regex = runCatching { Regex(ticketRegex.ifBlank { "[A-Z]+-\\d+" }) }.getOrNull() ?: return false
        return regex.matches(ticket.trim().uppercase())
    }

    fun sanitizeDescription(description: String): String =
        description
            .trim()
            .lowercase()
            .replace(invalidGitCharacters, "-")
            .replace(Regex("""[^a-z0-9._/-]+"""), "-")
            .replace(repeatedSeparators, "-")
            .trim('-', '.', '/')
            .ifBlank { "work" }

    private fun sanitizePathPart(value: String): String =
        sanitizeDescription(value).replace("/", "-")

    private fun sanitizeBranchName(value: String): String {
        var branch = value
            .replace(invalidGitCharacters, "-")
            .replace(Regex("""\.\.+"""), ".")
            .replace(repeatedSeparators, "/")
            .trim('/', '.', '-')


        while (branch.endsWith(".lock")) {
            branch = branch.removeSuffix(".lock").trim('.', '-', '/')
        }

        return branch.ifBlank { "feature/work" }
    }
}
