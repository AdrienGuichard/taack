package taackScheduller.visitor;

import taackScheduller.datum.Machine;
import taackScheduller.datum.Task;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public abstract class AbstractVisitor implements IVisitor {
    @Override
    public void addTask(String name) {

    }

    @Override
    public void addTaskEnd() {

    }

    @Override
    public void setExecutePath() {

    }

    @Override
    public void setExecutePathEnd() {

    }

    @Override
    public void addDistantProcess(Map<String, Machine> machineMap) {

    }

    @Override
    public void addDistantProcessEnd() {

    }

    @Override
    public void addLocalProcess() {

    }

    @Override
    public void exitOnError() {

    }

    @Override
    public void addLocalProcessEnd() {

    }

    @Override
    public void parallel(boolean quiteOnError) {

    }

    @Override
    public void parallelEnd() {

    }

    @Override
    public void sequential(boolean quiteOnError) {

    }

    @Override
    public void sequentialEnd() {

    }

    @Override
    public void addCommand(String command) {

    }

    @Override
    public void addCommand(File file) {

    }

    @Override
    public void addCommand(InputStream inputStream) {

    }

    @Override
    public void executeParallel() {

    }

    @Override
    public Map<String, Task> executeParallelEnd() {
        return null;
    }

    @Override
    public void executeSequential() {

    }

    @Override
    public void executeSequentialEnd() {

    }
}
