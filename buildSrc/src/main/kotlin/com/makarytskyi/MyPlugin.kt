package com.makarytskyi

import java.nio.file.Files
import java.nio.file.Paths
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

class MyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("myTask", CreatePullRequestTask::class.java)
    }
}

open class CreatePullRequestTask : DefaultTask() {

    private val branchName = project.findProperty("branch")?.toString() ?: "feature/added-generated-ids"

    @TaskAction
    fun createPullRequest() {
        Files.write(Paths.get("newFile.txt"), "Hello, World!".toByteArray())
        commitChanges()
        pushChanges()
    }

    private fun executeCommand(command: String): String {
        val process = ProcessBuilder(command.split(" ")).start()
        return process.inputStream.bufferedReader().readText()
    }

    private fun commitChanges() {
        executeCommand("git config --global user.email 'github-actions@github.com'")
        executeCommand("git config --global user.name 'github-actions'")
        executeCommand("git add .")
        executeCommand("git commit -m 'added generated ids'")
    }

    private fun pushChanges() {
        val gitPushCommand = "git push -u origin $branchName"
        executeCommand(gitPushCommand)
    }
}

//open class InvokeMethod : DefaultTask() {
//    @TaskAction
//    fun invokeMethod() {
//        val appContext = org.springframework.boot.builder.SpringApplicationBuilder()
//            .sources(Start::class.java)
//            .run()
//
//        val myService = appContext.getBean<MyComponent>("myComponent")
//
//        // Call the runTask method
//        myService.sayHello()
//
//        // Close the application context after use
//        appContext.close()
//    }
//}
