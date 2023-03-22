package run.gleam.gpm.project.workspace

/**
 * Defines a reason a package is in a project.
 */
enum class PackageOrigin {
    /**
     * The package is a part of our workspace.
     */
    WORKSPACE,

    /**
     * External dependency of [WORKSPACE] or other [DEPENDENCY] package
     */
    DEPENDENCY,
}