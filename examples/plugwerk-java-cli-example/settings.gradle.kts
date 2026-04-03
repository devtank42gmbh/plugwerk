rootProject.name = "plugwerk-java-cli-example"

// Composite build: resolve plugwerk-spi (and other io.plugwerk modules)
// directly from the main project — no publishToMavenLocal needed.
includeBuild("../..")

include("plugwerk-java-cli-example-api")
include("plugwerk-java-cli-example-app")
include("plugwerk-java-cli-example-hello-cmd-plugin")
include("plugwerk-java-cli-example-sysinfo-cmd-plugin")
