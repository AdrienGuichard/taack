package taackScheduller.datum

import groovy.transform.CompileStatic

@CompileStatic
final class Machine {
    int port
    String hostname
    String login
    Boolean sshMiddleServer
    Machine reverseSshServer

    String getName() {
        "$login@$hostname:$port"
    }
}
