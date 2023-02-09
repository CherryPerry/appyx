import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class ScreenshotTestPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.configure(CommonExtension::class.java) {
            defaultConfig {
                testInstrumentationRunnerArguments["useTestStorageService"] = "true"
            }
            testOptions.managedDevices.devices.register<ManagedVirtualDevice>(DEVICE_NAME) {
                device = "Pixel"
                apiLevel = 30
                systemImageSource = "aosp"
            }
        }

        target.dependencies.add("androidTestUtil", "androidx.test.services:test-services:1.4.2")

        target.tasks.register(TASK_RECORD, RecordScreenshotsTask::class.java) {
            dependsOn(project.tasks.named("${DEVICE_NAME}Check"))
            testResultsFolder.set(project.file("build/outputs/managed_device_android_test_additional_output/$DEVICE_NAME"))
        }

        target.tasks.register(TASK_COMPARE, CompareScreenshotsTask::class.java) {
            dependsOn(project.tasks.named("${DEVICE_NAME}Check"))
            testResultsFolder.set(project.file("build/outputs/managed_device_android_test_additional_output/$DEVICE_NAME"))
        }

    }

    companion object {
        const val DEVICE_NAME = "screenshotTestsDevice"
        const val GROUP = "screenshotTests"
        const val TASK_COMPARE = "screenshotTestsCompareBaseline"
        const val TASK_RECORD = "screenshotTestsRecordBaseline"
    }

}
