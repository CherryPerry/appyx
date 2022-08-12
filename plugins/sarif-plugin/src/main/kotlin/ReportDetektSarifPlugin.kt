import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ReportDetektSarifPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.withId("io.gitlab.arturbosch.detekt") {
            target.extensions.configure<DetektExtension> {
                baseline = target.file("detekt-baseline.xml")
                config.from(target.rootProject.file("detekt.yml"))
            }


            val detektTask = target.tasks.named("detekt", Detekt::class.java)
            detektTask.configure {
                reports.sarif.required.set(true)
            }

            val rootProject = target.rootProject
            rootProject.plugins.withId("appyx-collect-sarif") {
                rootProject.tasks.named(
                    CollectSarifPlugin.MERGE_DETEKT_TASK_NAME,
                    ReportMergeTask::class.java,
                ) {
                    input.from(
                        detektTask.map { it.sarifReportFile }.orNull
                    )
                    mustRunAfter(detektTask)
                }
            }
        }
    }

}