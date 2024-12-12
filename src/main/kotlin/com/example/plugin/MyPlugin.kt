package com.example.plugin

import com.example.logic.Application
import com.example.logic.Creator
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import com.example.logic.MyComponent
import org.gradle.api.tasks.JavaExec
import org.springframework.beans.factory.getBean
import org.springframework.boot.builder.SpringApplicationBuilder

open class MyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("commitPushTask", CommitPushTask::class.java)
        project.tasks.register("generateIds", GenerateIds::class.java)
    }
}


class CommitPushTask : DefaultTask() {

    private val branchName = project.findProperty(BRANCH_PROPERTY)?.toString() ?: DEFAULT_BRANCH

    @TaskAction
    fun commitAndPush() {
        commitChanges()
        pushChanges()
    }

    private fun executeCommand(command: String): String {
        val process = ProcessBuilder(command.split(" ")).start()
        return process.inputStream.bufferedReader().readText()
    }

    private fun commitChanges() {
        executeCommand(COMMIT_COMMAND)
    }

    private fun pushChanges() {
        val gitPushCommand = "git push -u origin $branchName"
        executeCommand(gitPushCommand)
    }

    companion object {
        private const val BRANCH_PROPERTY: String = "branch"
        private const val COMMIT_MESSAGE: String = "Automatically added files with generated ids"
        private const val DEFAULT_BRANCH: String = "feature/added-generated-ids"
        private const val COMMIT_COMMAND = "git add -A && git commit -m '$COMMIT_MESSAGE'"
    }
}

class GenerateIds : DefaultTask() {
    @TaskAction
    fun invokeMethod() {
        val appContext = SpringApplicationBuilder()
            .sources(MyComponent::class.java)
            .run()

        val myService = appContext.getBean<MyComponent>("myComponent")
        myService.createFile()
        appContext.close()
    }
}

fun main() {
    val appContext = SpringApplicationBuilder()
        .sources(Application::class.java)
        .run()

    val myService = appContext.getBean<MyComponent>("myComponent")
    myService.createFile()
    appContext.close()
}
