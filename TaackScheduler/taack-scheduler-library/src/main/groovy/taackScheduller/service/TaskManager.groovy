package taackScheduller.service

import groovy.transform.CompileStatic
import taackScheduller.common.TextIndent
import taackScheduller.datum.CommandResult
import taackScheduller.datum.Machine
import taackScheduller.datum.Task
import taackScheduller.service.log.FileAppender

import java.nio.file.Files
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@CompileStatic
final class TaskManager {
    final TextIndent o
    final ExecutorService executor

    TaskManager(TextIndent textIndent) {
        o = textIndent
        //executor = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors())
        o.printTabbed "proc# ${Runtime.getRuntime().availableProcessors()}"
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    }

    final class ProcessRuntime {
        final FileAppender out
        final FileAppender err
        final Process process

        ProcessRuntime(FileAppender out, FileAppender err, Process process) {
            this.out = out
            this.err = err
            this.process = process
        }
    }

    private void startTask(final Task task) {
        o.printIndent "startTask: ${task}"
        task.commands.find { command ->
            File cmds
            if (command.script) {
                cmds = command.script
            } else {
                cmds = Files.createTempFile("cmds", ".txt").toFile()
                cmds.deleteOnExit()
                if (command.commandLine) cmds.append(command.commandLine)
                else cmds.append(command.resource)
            }
            if (command.machines) {
                MachineService.startReverseSsh(command.machines)
                Map<String, ProcessRuntime> processes = [:]

                command.machines.each { entry ->
                    Machine machine = entry.value
                    Appendable out = new FileAppender(task.name, "out-${entry.key}")
                    Appendable err = new FileAppender(task.name, "err-${entry.key}")

                    o.printTabbed "executing command ${cmds}"
                    ProcessBuilder pb = new ProcessBuilder("ssh",
                            "${machine.login ? "${machine.login}@" : ""}${machine.hostname}",
                            " -p ${machine.port ?: 22}",
                            "'bash'"
                    )
                    pb.redirectInput(cmds)
                    Process p = pb.start()
                    p.consumeProcessOutput(out, err)
                    processes.put(entry.key, new ProcessRuntime(out, err, p))
                }

                while (processes.size() > 0) {
                    final Map<String, ProcessRuntime> tmp = new HashMap<>(processes)
                    tmp.each {
                        if (it.value.process.waitFor(1, TimeUnit.SECONDS)) {
                            int exitValue = it.value.process.exitValue()
                            command.results.put "${task.name}-${it.key}" as String,
                                    new CommandResult(task.name, exitValue, it.value.out, it.value.err, command.machines[it.key])
                            processes.remove(it.key)
                        }
                    }
                }
            } else {
                ProcessBuilder pb = new ProcessBuilder("bash")
                pb.redirectInput(cmds)
                o.printTabbed "executing command $cmds"
                Process p = pb.start()
                Appendable out = new FileAppender(task.name, "out-local")
                Appendable err = new FileAppender(task.name, "err-local")
                p.waitForProcessOutput(out, err)
                command.results.put "${task.name}-local" as String, new CommandResult(task.name, p.exitValue(), out, err, null)
            }
            if (task.exitOnError && command.results.values()*.retCode.find { it != 0 }) {
                o.printTabbed("exitOnError occurred")
                true // break
            } else false // continue
        }

        o.printDeIndent " startTaskEnd:  ${task}"
    }

    private List<Future<?>> scheduleTasks(Collection<Task> tasks) {
        o.printIndent "scheduleTasks ${tasks.size()} ${Thread.currentThread().getName()}"
        List<Future<?>> futureList = []
        tasks.each { task ->
            Future<?> f = executor.submit {
                startTask(task)
            }
            futureList.add(f)
        }

        o.printDeIndent "scheduleTaskEnd"
        return futureList
    }

    void execute(Collection<Task> tasks) {
        List<Future<?>> futureList = scheduleTasks(tasks)
        try {
            while (futureList.size() > 0) {
                final List<Future<?>> nextList = new ArrayList<Future<?>>(futureList)
                sleep(3000)
                nextList.each {
                    if (it.done) {
                        futureList.remove(it)
                    }
                }
            }
            MachineService.stopAll()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}
