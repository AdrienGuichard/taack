package taackScheduller.datum

import groovy.transform.CompileStatic

@CompileStatic
class Task {
    public String name
    public Stack<Command> commands = new Stack<>()
    public boolean exitOnError = false

    List<CommandResult> commandResults(String machineName = null) {
        commands.results["${name}-${machineName ?: 'local'}"].findAll { it != null } as List<CommandResult>
    }
}
