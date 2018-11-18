package taackScheduller.visitor.render;

import taackScheduller.common.TextIndent;
import taackScheduller.datum.Machine;
import taackScheduller.datum.Task;
import taackScheduller.visitor.AbstractVisitor;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public class Dump extends AbstractVisitor {
    public final TextIndent o;

    public Dump(TextIndent o) {
        this.o = o;
    }

    public Dump() {
        this.o = new TextIndent("");
    }

    @Override
    public void addTask(String name) {
        o.printIndent("addTask");
    }

    @Override
    public void addTaskEnd() {
        o.printDeIndent("addTaskEnd");
    }

    @Override
    public void setExecutePath() {
        o.printIndent("setExecutePath");
    }

    @Override
    public void setExecutePathEnd() {
        o.printDeIndent("setExecutePathEnd");
    }

    @Override
    public void addDistantProcess(Map<String, Machine> machineMap) {
        o.printIndent("addDistantProcess " + machineMap);
    }

    @Override
    public void addDistantProcessEnd() {
        o.printDeIndent("addDistantProcessEnd");
    }

    @Override
    public void addLocalProcess() {
        o.printIndent("addLocalProcess");
    }

    @Override
    public void exitOnError() {
        o.printTabbed("exitOnError");
    }

    @Override
    public void addLocalProcessEnd() {
        o.printDeIndent("addLocalProcessEnd");
    }

    @Override
    public void parallel(boolean quiteOnError) {
        o.printIndent("parallel");
    }

    @Override
    public void parallelEnd() {
        o.printDeIndent("parallelEnd");
    }

    @Override
    public void sequential(boolean quiteOnError) {
        o.printIndent("sequential");
    }

    @Override
    public void sequentialEnd() {
        o.printDeIndent("sequentialEnd");
    }

    @Override
    public void addCommand(String command) {
        o.printIndent("addCommand +++");
        o.printTabbed(command);
        o.printDeIndent("addCommand ---");
    }

    @Override
    public void addCommand(File file) {
        o.printTabbed("addCommand " + file.getName());
    }

    @Override
    public void addCommand(InputStream inputStream) {
        o.printIndent("addCommand " + inputStream);
    }

    @Override
    public void executeParallel() {
        o.printIndent("executeParallel");
    }

    @Override
    public Map<String, Task> executeParallelEnd() {
        o.printDeIndent("executeParallelEnd");
        return null;
    }

    @Override
    public void executeSequential() {
        o.printIndent("executeSequential");
    }

    @Override
    public void executeSequentialEnd() {
        o.printDeIndent("executeSequentialEnd");
    }
}
