package taackScheduller.spec

import groovy.transform.CompileStatic
import taackScheduller.visitor.IVisitor

@CompileStatic
class JobSpec {
    IVisitor visitor

    static Closure addTask(@DelegatesTo(value = TaskSpec, strategy = Closure.DELEGATE_ONLY) Closure closure) {
        closure
    }

    void setExecutionPath(@DelegatesTo(value = ExecuteSpec, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        visitor.setExecutePath()
        closure.delegate = new ExecuteSpec(visitor: visitor)
        closure.call()
        visitor.setExecutePathEnd()
    }
}
