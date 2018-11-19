package tasks

import groovy.transform.CompileStatic
import taackScheduller.Job
import taackScheduller.datum.CommandResult
import taackScheduller.datum.Machine
import taackScheduller.datum.Task
import taackScheduller.visitor.render.Execute

@CompileStatic
class SimpleSample implements Job {

    @Override
    Closure defineJob() {
        Machine ovh = new Machine()
        ovh.with {
            hostname = "xx.xx.xx.xx"
            login = "someoune"
            port = 2222
        }

        Machine nuccn = new Machine()
        nuccn.with {
            hostname = "localhost"
            login = "someLogin"
            port = 56755
            reverseSshServer = ovh
        }

        Machine nucde = new Machine()
        nucde.with {
            hostname = "localhost"
            login = "someOtherLogin"
            port = 11110
            reverseSshServer = ovh
        }

        Machine nucus = new Machine()
        nucus.with {
            hostname = "localhost"
            login = "xxx"
            reverseSshServer = ovh
        }

        setJob {
            def t1 = addTask {
                addDistantProcess([nucde: nucde], {
                    addCommand """
                        uptime
                        echo OK
                        """
                })

                // further errors will make task to exit
                exitOnError()

                addLocalProcess {
                    addCommand """
                        ls 
                        echo toto
                        exit 1
                        """
                    addCommand getClass().getResourceAsStream("/ls.sh")
                }
            }

            setExecutionPath {
                Map<String, Task> res = executeParallel([tFirst: t1])
                Task tFirst = res['tFirst'] as Task
                List<CommandResult> nucdeResults = tFirst?.commandResults('nucde')
                if (nucdeResults.first().retCode != 0) {
                    println "problem executing tFirst on nucde"
                } else {
                    println "no problem on nucde"
                    // executeParallel([t2: t1])
                }
                List<CommandResult> localResult = tFirst?.commandResults()
                List<File> totoOutput =  localResult*.out*.fileOutput.findAll {
                    File content -> content.text ==~ /(?sm).*toto.*/
                }
                if (totoOutput*.text)
                    println "echo toto OK"
            }
        }
    }

    static void main(String[] args) {
        new SimpleSample().execute new Execute()
        System.exit(0)
    }
}
