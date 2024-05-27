// Top-level build file where you can add configuration options common to all sub-projects/modules.
import org.apache.tools.ant.taskdefs.condition.Os
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    kotlin("jvm") version "1.5.21"
    id("com.android.library") version "8.1.0" apply false
}
val isWindows: Boolean by lazy {
    Os.isFamily(Os.FAMILY_WINDOWS)
}

tasks.register("convertDimens") {
    group = "custom"

    doLast {
        println("Simulating the conversion of dimens.xml...")
        // Simulate the conversion process here
    }
}

fun isSkipCheck(value: String): Boolean {
    return value.contains("sp") || value.contains("@dimen/")
}

fun createOrCloneDefaultData(xmlFileDefault: String, xmlFile360: String) {
    val fileDefault = File(xmlFileDefault)
    if (!fileDefault.exists()) {
        fileDefault.createNewFile()
        val content = buildString {
            append("<resources>\n")
            for (i in 2..100 step 2) {
                append("\t<dimen name=\"dp$i\">$i" + "dp</dimen>\n")
            }
            append("</resources>")
        }
        fileDefault.writeText(content)
    }

    val file360 = File(xmlFile360)
    if (fileDefault.exists()) {
        // Simulate copying default data to other files
        println("Copying default data to other files...")
    }
}

fun round(d: Float, decimalPlace: Int): Float {
    var bd = BigDecimal(d.toString())
    bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP)
    return bd.toFloat()
}

fun makeFolder(vararg paths: String) {
    paths.forEach {
        val folder = File(File(it).parent)
        if (!folder.exists()) {
            folder.mkdirs()
        }
    }
}

fun copyFileUsingStream(source: File, dest: File) {
    source.inputStream().use { input ->
        dest.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}
