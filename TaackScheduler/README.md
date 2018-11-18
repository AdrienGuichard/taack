**TaackScheduler** is a simple task scheduler using groovy closures to describe jobs, tasks, and machines definitions. It can be integrated to native OS platform service launcher (SystemD, cron, init ...) easily, or to other app (native app, Java web server or applications). It requires java, ssh and bash.

The motivations behind this project is to have a single source file describing jobs in a condensed manner, and to allow simple program to describe entities (machines, tasks, execution control flow, even jobs). The usage of closures is now well supported on recent IDE, providing easy code completion and code static code analysis. 

## Pros:  

- Build with gradle
- IDEs offer auto-completion of closures
- Job definition are statically analyzed when coding or building
- Fine grained error control flow
- Single file Job (a groovy class), meaning direct global view of your jobs
- Reliable, no task dependencies implying random execution flow
- Only requires ssh, bash and java
- No database backend, jobs can be versioned using regular SCM 
- Can be embedded easily into other app

## Cons:

- Error control flow requires development skills
- No tasks dependencies, control flow not optimized for performance
- Auto-completion not perfect with closures (maps)
- Requires an IDE supporting groovy @DelegatesTo annotation


## Usage:

Create a gradle project adding `taack-scheduler-library` dependency (see sample project `taack-scheduler-sample`).

Define a class that implements `taackScheduller.Job`. 

Call execute() method on the previous class instance.

That’s All

## Sample:


```groovy
@Override
Closure defineJob() {
    Machine ovh = new Machine()
    ovh.with {
        hostname = "xx.xx.xx.xx"
        login = "tartampion"
        port = 1234
    }

    // Intel nuc Shanghai server
    Machine nuccn = new Machine()
    nuccn.with {
        hostname = "localhost"
        login = "xxx"
        port = 55050
        reverseSshServer = ovh
    }
    //[. . .]
    setJob {
        def t1 = addTask {
            addDistantProcess([ nuccn: nuccn, 
                                nucus: nucus, 
                                nucde: nucde], {
                addCommand "uptime"
            })
            
            exitOnError()
            
            addLocalProcess {
                addCommand """
                        ls
                        echo toto
                        exit 1
                    """
            }
            addLocalProcess {
                addCommand getClass().getResourceAsStream("/ls.sh")
                addCommand new File("someFile")
            }
        }

        setExecutionPath {
            executeParallel([tFirst: t1])
        }
    }
}
```

Notes: 
- Support reverse ssh tunnel
- Maps are used to name tasks and machines, the key of the map is used to name output files. Commands are not named, file output names for commands are just suffixed with a time stamp.
- `addDistantProcess` execute distant process in parallel on distant machines.
- `addCommand` are executed sequentially
- the real execution starts at `executeParallel` method call. The job waits for all tasks to finish.
- command outputs (stdout and stderr) are stored in files in `tFirst/nuc*` for distant process and in `tFirst/local-*` for local process
- it is a good practice to pack scripts, sql, or other resources in `resources` folder to benefite from IDE code completion and SCM versioning. Files in `resources` folder are packed into the binary, simplifying deployment.
- `exitOnError` break the current task if a command exit code is not 0 but the job continues
## Control flow example
```groovy
    // [. . .]            
    setExecutionPath {
        Map<String, Task> res = executeParallel([tFirst: t1])
        Task tFirst = res['tFirst']
        List<CommandResult> nucdeResults = tFirst?.commandResults('nucde')
        if (nucdeResults.first().retCode != 0) {
            println "problem executing uptime on nucde"
        } else {
            println "no problem on nucde"
            // executeParallel([t2: t1])
        }
        List<CommandResult> localResult = tFirst?.commandResults()
        List<File> totoOutput =  localResult*.out*.fileOutput.findAll { File content -> content.text ==~ /(?sm).*toto.*/ }
        if (totoOutput*.text)
            println "echo toto OK"
    }

```
This code sample shows that you can use retCode from process or process outputs to control execution flow.

## Execution

Either use `./gradlew run` on the root of the project, or if you want to generate binaries `./gradlew assembleDist` then `cd build/distributions` copy past somewhere the zip file, unzip, and go to bin directory. The code will execute faster this way.

When you use Intellij, open the project using New > From existing source. Then choose gradle project. Auto-completion will come soon after the gradle build.

## Notes on future improvements

The RAM consumed takes around 100 megs for the SampleApp. It should be possible to reduce greatly the amount of RAM consumed by using Java 9 Modules. But ATM Linux distributions manly ship with OpenJDK 1.8.

Each reverse ssh tunnel has to be initialized before the target machine could be reach. We set a timeout of 3 seconds after the ssh process starts. In future version, it would be wise to avoid this time out.