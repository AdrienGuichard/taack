package taackScheduller.datum

import groovy.transform.CompileStatic
import taackScheduller.service.log.FileAppender

@CompileStatic
final class CommandResult {
    final String name
    final int retCode
    final FileAppender out
    final FileAppender err
    final Machine machine

    CommandResult(String name, int retCode, FileAppender out, FileAppender err, Machine machine) {
        this.name = name
        this.retCode = retCode
        this.out = out
        this.err = err
        this.machine = machine
    }
}

