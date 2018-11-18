package taackScheduller.spec

import groovy.transform.CompileStatic
import taackScheduller.datum.Machine
import taackScheduller.visitor.IVisitor

@CompileStatic
class TaskSpec {
    IVisitor visitor

    void addDistantProcess(Map<String, Machine> machineMap,
                           @DelegatesTo(value = ProcessSpec, strategy = Closure.DELEGATE_ONLY) Closure process) {
        visitor.addDistantProcess(machineMap)
        process.delegate = new ProcessSpec(visitor: visitor)
        process.call()
        visitor.addDistantProcessEnd()
    }

    void addLocalProcess(@DelegatesTo(value = ProcessSpec, strategy = Closure.DELEGATE_ONLY) Closure process) {
        visitor.addLocalProcess()
        process.delegate = new ProcessSpec(visitor: visitor)
        process.call()
        visitor.addLocalProcessEnd()
    }

    void exitOnError() {
        visitor.exitOnError()
    }
}
