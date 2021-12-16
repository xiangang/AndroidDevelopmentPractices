package com.nxg.plugins

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


const val METHOD_ON_DOWNGRADE = "onDowngrade"
const val METHOD_ON_UPGRADE = "onUpgrade"
const val CLASS_ROOM_OPEN_HELPER = "androidx/room/RoomOpenHelper.class"

/**
 * 访问class
 */
class RoomOpenHelperClassVisitor constructor(
    classWriter: ClassWriter,
    private val className: String
) : ClassVisitor(Opcodes.ASM6, classWriter) {

    private var mClassName: String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        mClassName = name
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        desc: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        //println("RoomOpenHelperClassVisitor mClassName $mClassName className $className name $name")
        val methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
        if (className == mClassName && METHOD_ON_DOWNGRADE == name) {
            //返回自定义MethodVisitor对象
            return RoomOpenHelperMethodVisitor(methodVisitor, access, name, desc)
        }
        return methodVisitor
    }


}

/**
 * 访问method
 */
class RoomOpenHelperMethodVisitor constructor(
    methodVisitor: MethodVisitor,
    access: Int,
    name: String,
    descriptor: String?
) : AdviceAdapter(Opcodes.ASM6, methodVisitor, access, name, descriptor) {

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        desc: String?,
        itf: Boolean
    ) {
        //移除METHOD_ON_UPGRADE方法调用
        if (opcode == INVOKEVIRTUAL && METHOD_ON_UPGRADE == name) {
            println("rm onUpgrade(db, oldVersion, newVersion) and add updateIdentity(db)")
            //新增updateIdentity方法调用
            mv.visitVarInsn(ALOAD, 0)
            mv.visitVarInsn(ALOAD, 1)
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "androidx/room/RoomOpenHelper",
                "updateIdentity",
                "(Landroidx/sqlite/db/SupportSQLiteDatabase;)V",
                false
            )
            return
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf)

    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("$CLASS_ROOM_OPEN_HELPER $METHOD_ON_DOWNGRADE start");
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/String;)V",
            false
        );
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("$CLASS_ROOM_OPEN_HELPER $METHOD_ON_DOWNGRADE end");
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/String;)V",
            false
        );
    }
}


/**
 *Transform扫描class
 */
class RoomOpenHelperTransform internal constructor(private val project: Project) : Transform() {
    override fun getName(): String {
        return "RoomOpenHelperTransform"
    }

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope>? {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return false
    }

    @Throws(TransformException::class, InterruptedException::class, IOException::class)
    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        println("\nRoomOpenHelperTransform start to transform-------------->>>>>>>")
        val outputProvider = transformInvocation.outputProvider
        val isIncremental = transformInvocation.isIncremental
        println("RoomOpenHelperTransform isIncremental is $isIncremental-------------->>>>>>>")
        //如果非增量，则清空旧的输出内容
        if (!isIncremental) {
            println("RoomOpenHelperTransform outputProvider delete all-------------->>>>>>>")
            outputProvider.deleteAll()
        }
        val inputs = transformInvocation.inputs
        for (transformInput in inputs) {
            //遍历所有的class文件目录
            val directoryInputs = transformInput.directoryInputs
            for (directoryInput in directoryInputs) {
                //必须这样获取输出路径的目录名称
                val destFile = transformInvocation.outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )
                FileUtils.copyDirectory(directoryInput.file, destFile)
            }
            val jarInputs = transformInput.jarInputs
            for (jarInput in jarInputs) {
                //获取输出路径下的jar包名称，必须这样获取，得到的输出路径名不能重复，否则会被覆盖
                val destFile = transformInvocation.outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                if (jarInput.file.absolutePath.endsWith(".jar")) {
                    //println("RoomOpenHelperTransform: jarInput.file.absolutePath = " + jarInput.file.absolutePath);
                    val jarFile = jarInput.file
                    //只处理有我们业务逻辑的jar包
                    if (shouldProcessPreDexJar(jarFile.absolutePath)) {
                        handleJar(jarFile, destFile)
                        continue
                    }
                }
                //将输入文件拷贝到输出目录下
                FileUtils.copyFile(jarInput.file, destFile)
            }
        }
    }

    companion object {

        private fun shouldProcessPreDexJar(path: String): Boolean {
            return path.contains("room-runtime")
        }

        private fun handleJar(jarFile: File, destFile: File) {
            println("RoomOpenHelperTransform: handleJar ${jarFile.absolutePath}")
            val zipFile = ZipFile(jarFile)
            val zipOutputStream = ZipOutputStream(FileOutputStream(destFile))
            zipOutputStream.use {
                zipFile.use {
                    val enumeration = zipFile.entries()
                    while (enumeration.hasMoreElements()) {
                        val zipEntry = enumeration.nextElement()
                        val zipEntryName = zipEntry.name
                        //println("RoomOpenHelperTransform: handleJar zipEntryName $zipEntryName")
                        if (CLASS_ROOM_OPEN_HELPER == zipEntryName) {
                            val inputStream = zipFile.getInputStream(zipEntry)
                            val classReader = ClassReader(inputStream)
                            println("RoomOpenHelperTransform: handleJar classReader.className ${classReader.className}")
                            val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                            val classVisitor: ClassVisitor =
                                RoomOpenHelperClassVisitor(classWriter, classReader.className)
                            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)

                            val data = classWriter.toByteArray()
                            val byteArrayInputStream: InputStream = ByteArrayInputStream(data)

                            val newZipEntry = ZipEntry(zipEntryName)
                            FileUtil.addZipEntry(zipOutputStream, newZipEntry, byteArrayInputStream)

                        } else {
                            val newZipEntry = ZipEntry(zipEntryName)
                            FileUtil.addZipEntry(
                                zipOutputStream,
                                newZipEntry,
                                zipFile.getInputStream(zipEntry)
                            )
                        }

                    }
                }
            }
        }

        private fun eachFileRecurse(file: File) {
            if (file.exists()) {
                val files = file.listFiles()
                if (null != files) {
                    for (tempFile in files) {
                        if (tempFile.isDirectory) {
                            eachFileRecurse(tempFile)
                        } else {
                            if (tempFile.name == "module-info.class") {
                                println("RoomOpenHelperTransform: class file name = module-info.class , delete ")
                            }
                        }
                    }
                }
            }
        }

        private fun handleSources(directoryInput: DirectoryInput) {
            directoryInput.file.walkTopDown().filter { it.isFile }.forEach {
                if (CLASS_ROOM_OPEN_HELPER == it.name) {
                    it.inputStream().use { inputStream ->
                        val classReader = ClassReader(inputStream)
                        println("handleSources->${it.absolutePath}, name->${classReader.className}")
                        val classWriter = ClassWriter(
                            classReader,
                            ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS
                        )
                        val classVisitor: ClassVisitor =
                            RoomOpenHelperClassVisitor(classWriter, classReader.className)
                        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                        it.outputStream().use { outputStream ->
                            outputStream.write(classWriter.toByteArray())
                        }
                    }
                }
            }
        }

    }
}
