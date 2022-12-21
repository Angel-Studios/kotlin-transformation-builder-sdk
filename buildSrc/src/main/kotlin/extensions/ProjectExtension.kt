package extensions

import org.gradle.api.Project
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * Read the current git tag.
 * CommandLine execution replicates `git tag --points-at HEAD | sort -V | tail -n1`
 *
 * @return the git tag for the current commit or 0.0.0
 */
fun Project.getGitTag(): String {
    var stdout = ByteArrayOutputStream()
    project.exec {
        commandLine("git", "tag", "--points-at", "HEAD")
        standardOutput = stdout
    }

    var stdin = ByteArrayInputStream(stdout.toByteArray())
    stdout = ByteArrayOutputStream()
    project.exec {
        standardInput = stdin
        standardOutput = stdout
        commandLine("sort", "-V")
    }

    stdin = ByteArrayInputStream(stdout.toByteArray())
    stdout = ByteArrayOutputStream()
    project.exec {
        standardInput = stdin
        standardOutput = stdout
        commandLine("tail", "-n1")
    }

    return stdout.toString()
        .ifBlank {
            val now = Calendar.getInstance()
            val year = now.get(Calendar.YEAR).toString().drop(2)
            val weekOfYear = now.get(Calendar.WEEK_OF_YEAR) - 1
            "$year.$weekOfYear.999"
        }
        .trim()
        .let { out -> out.substring(out.lastIndexOf("-") + 1) }
}