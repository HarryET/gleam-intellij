package run.gleam

interface GleamTestCase : TestCase {
    override val testFileExtension: String get() = "gleam"
}