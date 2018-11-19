package taackScheduller.service

import groovy.transform.CompileStatic
import taackScheduller.datum.Machine

@CompileStatic
final class MachineService {
    static final Map<Machine, Integer> machineConnectionCounter = [:]
    static final Map<Machine, Process> openConnectionProcess = [:]

    static void startReverseSsh(final Machine machine) {
        if (machine.reverseSshServer) {
            String command = "/usr/bin/ssh -p ${machine.reverseSshServer.port ?: 22} ${machine.reverseSshServer.login}@${machine.reverseSshServer.hostname} " +
                    " -L ${machine.port}:${machine.hostname}:${machine.port} -N"
            Process process = command.execute()

            openConnectionProcess.put(machine, process)

            sleep(3000)
        }
    }

    static void startReverseSsh(final Map<String, Machine> machineMap) {
        machineMap.values().grep { Machine machine ->
            machine.reverseSshServer != null
        }.each {
            if (machineConnectionCounter.containsKey(it)) {
                Integer instanceCounter = machineConnectionCounter.get(it)
                if (instanceCounter == 0) {
                    startReverseSsh(it)
                }
                machineConnectionCounter.put(it, instanceCounter + 1)
            } else {
                machineConnectionCounter.put(it, 1)
                startReverseSsh(it)
            }
        }
    }

    static void stopAll() {
        openConnectionProcess.values().each { Process p ->
            if (p.alive) {
                p.destroy()
            }
        }
        openConnectionProcess.clear()
        machineConnectionCounter.clear()
    }
}
