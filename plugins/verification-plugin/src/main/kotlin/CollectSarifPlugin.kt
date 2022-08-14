import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import java.io.File

class CollectSarifPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tasks.register(MERGE_LINT_TASK_NAME, ReportMergeTask::class.java) {
            group = JavaBasePlugin.VERIFICATION_GROUP
            output.set(project.layout.buildDirectory.file("lint-merged.sarif"))
        }
        target.tasks.register(MERGE_DETEKT_TASK_NAME, ReportMergeTask::class.java) {
            group = JavaBasePlugin.VERIFICATION_GROUP
            output.set(project.layout.buildDirectory.file("detekt-merged.sarif"))
            enforceErrorLevel()
        }
    }

    private fun ReportMergeTask.enforceErrorLevel() {
        doLast {
            val rootDir = project.rootProject.rootDir.absolutePath
            val outputFile = output.asFile.get()
            outputFile.writeText(
                outputFile
                    .readText()
                    .replace("\"level\": \"warning\",", "\"level\": \"error\",")
                    .replace(rootDir + File.separator, ""),
            )
        }
    }

    companion object {
        const val MERGE_LINT_TASK_NAME: String = "mergeLintSarif"
        const val MERGE_DETEKT_TASK_NAME: String = "mergeDetektSarif"
    }
}
