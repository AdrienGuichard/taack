package taackScheduller.visitor.render;

import taackScheduller.datum.Command;
import taackScheduller.datum.Machine;
import taackScheduller.datum.Task;
import taackScheduller.service.TaskManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class Execute extends Dump {
    private Map<String, Task> tasks = new HashMap<>();
    private Task currentTask;
    private TaskManager taskManager;
    private Map<String, Machine> machineMap;

    @Override
    public void addTask(String name) {
        currentTask = new Task();
        currentTask.name = name;
        super.addTask(name);
    }

    @Override
    public void addTaskEnd() {
        tasks.put(currentTask.name, currentTask);
        super.addTaskEnd();
    }

    @Override
    public void setExecutePath() {
        taskManager = new TaskManager(super.o);
        super.setExecutePath();
    }

    @Override
    public void setExecutePathEnd() {
        super.setExecutePathEnd();
    }

    @Override
    public void addDistantProcess(Map<String, Machine> machineMap) {
        this.machineMap = machineMap;
        super.addDistantProcess(machineMap);
    }

    @Override
    public void addDistantProcessEnd() {
        this.machineMap = null;
        super.addDistantProcessEnd();
    }

    @Override
    public void addLocalProcess() {
        machineMap = null;
        super.addLocalProcess();
    }

    @Override
    public void addCommand(String command) {
        Command c = new Command();
        c.machines = machineMap;
        c.commandLine = command;
        currentTask.commands.add(c);
        super.addCommand(command);
    }

    @Override
    public void addCommand(InputStream inputStream) {
        Command c = new Command();
        c.machines = machineMap;
        c.resource = inputStream;
        currentTask.commands.add(c);
        super.addCommand(inputStream);
    }

    @Override
    public void addCommand(File file) {
        Command c = new Command();
        c.machines = machineMap;
        c.script = file;
        currentTask.commands.add(c);
        super.addCommand(file);
    }

    @Override
    public void exitOnError() {
        currentTask.exitOnError = true;
        super.exitOnError();
    }

    @Override
    public void executeParallel() {
        tasks.clear();
        super.executeParallel();
    }

    @Override
    public Map<String, Task> executeParallelEnd() {
        taskManager.execute(tasks.values());
        super.executeParallelEnd();
        return tasks;
    }

    @Override
    public void executeSequential() {
        tasks.clear();
        super.executeSequential();
    }

    @Override
    public void executeSequentialEnd() {
        taskManager.execute(tasks.values());
        super.executeSequentialEnd();
    }
}
