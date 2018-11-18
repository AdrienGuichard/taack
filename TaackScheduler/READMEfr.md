**TaackScheduler** est un planificateur de tâches utilisant les closures groovy pour décrire le référentiel de données (tâches, flow d'exécution, et machines) des travaux à exécuter. Il peut être intégré facilement aux lanceurs de services natifs de nos OS préférés (SystemD, cron, init ...), ou a d'autres applications (natives ou Java). 

Il requiert java, ssh et bash.

## Pros:  

- Construit avec gradle en configuration multi-projet, facilement réutilisable
- Support des IDE des closures
- Compilation statique et analyse statique
- Contrôle fin du flux d'exécution
- Fichier unique pour toutes les entités, permettant d'avoir une vue global rapidement
- Pas de dépendances entre les tâches, le fux d'exécution est facile à prévoir
- Requiert seulement ssh, bash et Java
- Pas de base de données en backend, versionnage du code et des sorties avec n'importe quel SCM

## Cons:

- Requiert des compétences en développement pour la gestion des erreurs
- pas de dépendances entre les tâches implique que le flux d'exéction n'est pas optimal
- Requière un IDE supportant l'annotation groovy @DelegatesTo


## Utilisation:

Créer un projet gradle ajoutant la dépendance `taack-scheduler-library` (voir le project `taack-scheduler-sample`, le dupliquer et adapter le fichier gradle).

Définir une classe qui implémente `taackScheduller.Job`. 

Appeler `execute()` sur l'instance de la classes précédente.


## Exemple:


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

- Supporte les 'reverse ssh tunnel'
- Les maps sont utilisées pour les noms de tâches et de machines, la clé de la map est utilisée pour le nommage des fichiers contenant `stdout` et `stderr`. Les commandes n'ont pas de nom, les fichiers sont suffixés avec un timestamp.
- `addDistantProcess` exécute les commandes séquentiellement sur des machines distantes en même temps (parallèlement)
- `addCommand` les commandes sont exécutées séquentiellement
- l'exécution commence à l'appel `executeParallel`. Le job attend l'achèvement de toutes les tâches
- ici, les sorties des commandes (stdout et stderr) sont stoquées dans les fichiers `tFirst/nuc*` pour les process distant, et dans `tFirst/local-*` pour les processes locaux
- on peut utiliser les ressources (répertoire `resources`) pour les scripts, le sql et autres pour profiter du SCM et de l'IDE, les fichiers ressources sont packés avec le jar du binaire.
- `exitOnError` sort de la tâche courante, mais le job continue

## Exemple de gestion d'erreur

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

On peut utiliser les codes de sortie des commandes, ou le contenu des sorties stderr ou stdout, et un peu de code pour gérer le flux d'exécution.


## Exécution

Utiliser soit `./gradlew run` ou, si vous voulez créer un binaire redistribuable `./gradlew assembleDist` puis `cd build/distributions` (exécution plus rapide du binaire distribuable)

Pour les utilisateurs d'Intellij, il faut ouvrir ce projet comme un projet Gradle. La version communautaire sera suffisante, avec quelques plugins pour la complétion du SQL, du Bash ou l'édition de CSV.

## Notes sur les améliorations futures

LA RAM consommée est d'environ 100 MB pour SampleApp. En utilisant l'approche modulaire de Java 9 on devrait pouvoir réduire fortement cette consommation. Pour le moment la plupart des distributions sont livrées avec Java 8 et 100 MB, on peut vivre avec.

Il y a un sleep de 3 secondes pour initialiser chaque reverse ssh tunnel. Il faudrait l'éviter, mais je ne sais pas comment faire simplement. Par contre, l'initialisation des tunnels n'est faite qu'une fois, on réutilise le même reverse ssh tunnel jusqu'à la fin du job. 