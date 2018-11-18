package taackScheduller.datum

class Task {
    public String name
    public Stack<Command> commands = []
    public boolean exitOnError = false
    List<CommandResult> commandResults(String machineName = null) {
        commands.results["${name}-${machineName ?: 'local'}"].findAll { it != null } as List<CommandResult>
    }
}
