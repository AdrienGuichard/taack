package taackScheduller.datum

import groovy.transform.CompileStatic

@CompileStatic
final class Command {
    public Map<String, Machine> machines
    public Map<String, CommandResult> results = [:]
    public String commandLine
    public File script
    public InputStream resource
}
