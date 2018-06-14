package us.ascendtech.js.npm

import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.options.Option

import javax.inject.Inject

/**
 * @author Matt Davis
 * Apache 2.0 License
 * Based on https://github.com/solugo/gradle-nodejs-plugin
 */
class NpmTask<T extends NpmTask<T>> extends AbstractExecTask<T> {

    def baseCmd = ""
    def baseArgs = ""
    def argsSuffix = ""
    def npmModule = ""


    @Inject
    NpmTask() {
        this(NpmTask)
    }

    NpmTask(final Class<T> taskType) {
        super(taskType)
    }

    @Option(option = "npmModule", description = "npm module")
    public void setNpmArgs(String npmModule) {
        this.npmModule = npmModule
    }

    @Override
    protected void exec() {
        final NpmUtil nodeUtil = NpmUtil.getInstance(this.project.npm.nodeJsVersion)

        final commandLine = new ArrayList<String>()

        final String exec = nodeUtil.resolveCommand(baseCmd)
        if (exec == null) {
            throw new RuntimeException((String) "Cannot find " + baseCmd + ".  Is it npm installed?")
        }

        commandLine.add(exec)

        if (baseArgs != null) {
            commandLine.addAll(Arrays.asList(baseArgs.split()))
        }

        if (npmModule != null) {
            commandLine.addAll(npmModule.split())
        }

        if (argsSuffix != null) {
            commandLine.addAll(Arrays.asList(argsSuffix.split()))
        }

        println "Adding to path ${nodeUtil.bin.absolutePath}"

        try {
            this.environment 'PATH', "${nodeUtil.bin.absolutePath}:${environment.PATH}"
            this.environment 'Path', "${nodeUtil.bin.absolutePath}:${environment.Path}"
            this.commandLine(commandLine)
            println "Running " + commandLine
            super.exec()
        } catch (Exception ex) {
            throw new RuntimeException("Error running ${commandLine.join(" ")}", ex)
        }
    }

}
