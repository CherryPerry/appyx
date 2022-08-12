import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.Plugin
import org.gradle.api.Project

class DetektPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.withId("io.gitlab.arturbosch.detekt") {
            val detektTask = target.tasks.named("detekt", Detekt::class.java)
            detektTask.configure {
                reports.sarif.required.set(true)
                ignoreFailures = true
            }

            val rootProject = target.rootProject

            /*rootProject.plugins.withId("appyx-collect-lint-sarif") {
                val mergeTask = rootProject.tasks.named(
                    CollectLintSarifPlugin.MERGE_TASK_NAME,
                    SarifMergeTask::class.java,
                )

                rootProject.tasks.named(
                    CollectLintSarifPlugin.MERGE_TASK_NAME,
                    SarifMergeTask::class.java,
                ) {
                    lintSarifFiles.from(
                        detektTask.flatMap { it.sarifReportFile }
                    )
                    // for some reason automatic dependency does not works
                    mustRunAfter(detektTask)
                }
            }*/

            rootProject.plugins.withId("appyx-collect-lint-sarif") {
                rootProject.tasks.named(
                    CollectLintSarifPlugin.MERGE_TASK_NAME,
                    SarifMergeTask::class.java,
                ) {
                    lintSarifFiles.from(
                        detektTask.map { it.sarifReportFile }
                    )
                }
            }
        }
    }

}