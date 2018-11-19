package taackScheduller.datum

import groovy.transform.CompileStatic

@CompileStatic
final class Machine {
    Integer port
    String hostname
    String login
    Machine reverseSshServer

    String getName() {
        "$login@$hostname:${port?:22}"
    }
}
