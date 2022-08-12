import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin

class CollectLintSarifPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tasks.register(MERGE_TASK_NAME, ReportMergeTask::class.java) {
            group = JavaBasePlugin.VERIFICATION_GROUP
            output.set(project.layout.buildDirectory.file("lint-merged.sarif"))
        }
    }

    companion object {
        const val MERGE_TASK_NAME: String = "mergeLintSarif"
    }
}
