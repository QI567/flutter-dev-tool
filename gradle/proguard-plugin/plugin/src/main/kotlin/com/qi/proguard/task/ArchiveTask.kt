package com.qi.proguard.task

import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.Zip

@CacheableTask
abstract class ArchiveTask: Zip() {

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFiles
    abstract val srcFiles: Property<FileCollection>

    @get:OutputFile
    abstract val outFile: RegularFileProperty

}