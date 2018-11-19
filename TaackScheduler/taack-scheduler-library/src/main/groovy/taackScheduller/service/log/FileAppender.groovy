package taackScheduller.service.log

import groovy.transform.CompileStatic

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@CompileStatic
final class FileAppender implements Appendable {
    final File fileOutput

    FileAppender(final String taskName, final String namePrefix) {
        Path p = Paths.get("${taskName}/${namePrefix}-${System.currentTimeMillis()}.log")
        Files.createDirectories(p.parent)
        Files.createFile(p)
        this.fileOutput = p.toFile()
    }

    @Override
    Appendable append(CharSequence charSequence) throws IOException {
        fileOutput.append(charSequence)
        return this
    }

    @Override
    Appendable append(CharSequence charSequence, int start, int end) throws IOException {
        println("WARNING: ${charSequence}, ${start}, ${end}")
        return this
    }

    @Override
    Appendable append(char c) throws IOException {
        fileOutput.append(c)
        return this
    }
}
