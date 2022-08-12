import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.internal.lint.AndroidLintTask
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ReportLintSarifPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.withId("com.android.library") {
            collectLintSarif(target)
        }
        target.plugins.withId("com.android.application") {
            collectLintSarif(target)
        }
    }

    private fun collectLintSarif(target: Project) {
        target.extensions.configure<CommonExtension<*, *, *, *>>("android") {
            lint.sarifReport = true
        }
        val rootProject = target.rootProject
        rootProject.plugins.withId("appyx-collect-lint-sarif") {
            rootProject.tasks.named(
                CollectLintSarifPlugin.MERGE_TASK_NAME,
                ReportMergeTask::class.java,
            ) {
                input.from(
                    target
                        .tasks
                        .named("lintReportDebug", AndroidLintTask::class.java)
                        .flatMap { it.sarifReportOutputFile }
                )
            }
        }
    }

}
