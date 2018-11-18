package taackScheduller.spec

import groovy.transform.CompileStatic
import taackScheduller.visitor.IVisitor

@CompileStatic
class ProcessSpec {
    IVisitor visitor

    void parallel(boolean quiteOnError,
                  @DelegatesTo(value = ProcessSpec, strategy = Closure.DELEGATE_ONLY) Closure closure) {
        visitor.parallel(quiteOnError)
        closure.delegate = this
        closure.call()
        visitor.parallelEnd()
    }

    void sequential(boolean quiteOnError,
                    @DelegatesTo(value = ProcessSpec, strategy = Closure.DELEGATE_ONLY) Closure closure) {
        visitor.sequential(quiteOnError)
        closure.delegate = this
        closure.call()
        visitor.sequentialEnd()
    }

    void addCommand(String command) {
        visitor.addCommand(command)
    }

    void addCommand(File file) {
        visitor.addCommand(file)
    }

    void addCommand(InputStream inputStream) {
        visitor.addCommand(inputStream)
    }
}
