package taackScheduller.visitor;

import taackScheduller.datum.Machine;
import taackScheduller.datum.Task;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public interface IVisitor {
    void addTask(String name);

    void addTaskEnd();

    void setExecutePath();

    void setExecutePathEnd();

    void addDistantProcess(Map<String, Machine> machineMap);

    void addDistantProcessEnd();

    void addLocalProcess();

    void addLocalProcessEnd();

    void exitOnError();

    void parallel(boolean quiteOnError);

    void parallelEnd();

    void sequential(boolean quiteOnError);

    void sequentialEnd();

    void addCommand(String command);

    void addCommand(File file);

    void addCommand(InputStream inputStream);

    void executeParallel();

    Map<String, Task> executeParallelEnd();

    void executeSequential();

    void executeSequentialEnd();
}
