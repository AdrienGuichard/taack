package taackScheduller.spec

import groovy.transform.CompileStatic
import taackScheduller.datum.Task
import taackScheduller.visitor.IVisitor

@CompileStatic
class ExecuteSpec {
    IVisitor visitor

    Map<String, Task> executeParallel(Map<String, Closure> tasks) {
        visitor.executeParallel()
        tasks.each { entry ->
            visitor.addTask(entry.key)
            entry.value.delegate = new TaskSpec(visitor: visitor)
            entry.value.call()
            visitor.addTaskEnd()
        }
        visitor.executeParallelEnd()
    }

    void executeSequential(List<Closure> tasks) {
        visitor.executeSequential()
        int i = 0
        tasks.each { closure ->
            visitor.addTask("seq-${i++}")
            closure.delegate = new TaskSpec(visitor: visitor)
            closure.call()
            visitor.addTaskEnd()
        }
        visitor.executeSequentialEnd()
    }
}
