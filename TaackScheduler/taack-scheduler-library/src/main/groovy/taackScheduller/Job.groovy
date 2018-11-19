package taackScheduller

import taackScheduller.service.MachineService
import taackScheduller.spec.JobSpec
import taackScheduller.visitor.IVisitor

trait Job {
    Closure setJob(@DelegatesTo(value = JobSpec, strategy = Closure.DELEGATE_ONLY) Closure closure) {
        closure
    }

    abstract Closure defineJob();

    void execute(IVisitor visitor) {
        Closure c = defineJob()
        c.delegate = new JobSpec(visitor: visitor)
        c.call()
        MachineService.stopAll()
    }
}