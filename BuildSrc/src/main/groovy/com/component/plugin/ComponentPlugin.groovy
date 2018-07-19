package com.component.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.component.asm.ComponentInitVisitor
import com.component.asm.ComponentInsertVisitor
import com.component.asm.ComponentRouterVisitor
import com.component.asm.ComponentVisitor
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * 解析各个Component插件
 */
class ComponentPlugin extends Transform implements Plugin<Project> {

    private static final String EXTENSION_NAME = "componentConfig"

    Project project
    ComponentExtension componentConfig

    @Override
    void apply(Project project) {
        this.project = project

        project.extensions.create(EXTENSION_NAME, ComponentExtension)
//        project.task("hello", type: HelloTask)

//        project.extensions.create(EXTENSION_NAME, ComponentExtension)
        project.afterEvaluate {
            componentConfig = project.extensions.findByName(EXTENSION_NAME) as ComponentExtension
            project.logger.error("====== componentConfig = " + componentConfig.insertClassName)
            if (componentConfig.insertClassName == null) {// 指定模块要插入的类
                componentConfig.insertClassName = "ComponentController.class"
            }
            if (componentConfig.routerClassName == null) {// 指定路由要插入的类
                componentConfig.routerClassName = "RouterController.class"
            }
        }

        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(this)
    }

    @Override
    String getName() {
        return "ComponentPlugin"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * 不是R系列的class
     */
    boolean notRClass(String name) {
        return !name.endsWith("R.class") && !name.contains("R\$") && !name.contains("\$") && !name.endsWith("BuildConfig.class")
    }

    /**
     * 不是Support系列包
     */
    boolean notSupport(String name) {
        return !name.contains("android/support")
    }

    /**
     * 扫描所有的class文件
     */
    private boolean scanClassFiles(final byte[] b) {
        ClassReader cr = new ClassReader(b)
        ClassWriter cw = new ClassWriter(cr, 0)
        ComponentVisitor visitor = new ComponentVisitor(Opcodes.ASM5, cw)
        cr.accept(visitor, ClassReader.EXPAND_FRAMES)

        if (visitor.annotationVisitor != null && visitor.annotationVisitor.annotationName != null) {
            components.add(visitor)// 加入列表
        }
        if (visitor.routerAnnotationVisitor != null && visitor.routerAnnotationVisitor.annotationName != null) {
            routers.add(visitor)// 加入路由列表
        }

        // 返回是否需要解析Intent数据
        return visitor.isContainIntentData
    }

    /**
     * 往目录中的class文件插入相对应的getIntent代码
     */
    private void insertClassFileForDirectory(final File file) {
        // 需要将生产的新class覆盖本地的class文件然后再输出
        byte[] code = getClassByte(file.bytes)
        FileOutputStream fos = new FileOutputStream(file.parentFile.absolutePath + File.separator + file.name)
        fos.write(code)
        fos.close()
    }

    private byte[] getClassByte(byte[] b) {
        ClassReader cr = new ClassReader(b)
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        ComponentInitVisitor visitor = new ComponentInitVisitor(Opcodes.ASM5, cw)
        cr.accept(visitor, ClassReader.EXPAND_FRAMES)

        // 需要将生产的新class覆盖本地的class文件然后再输出
        return cw.toByteArray()
    }

    /**
     * 往jar目录的class中插入getIntent代码
     */
    private File insertClassFileForJar(final File file) {
        File tempFile = new File(file.getParent() + File.separator + "component_intent.jar")
        if (tempFile.exists()) {
            tempFile.delete()
        }
        FileOutputStream fos = new FileOutputStream(tempFile)
        JarOutputStream jarOutputStream = new JarOutputStream(fos)

        JarFile jarFile = new JarFile(file)
        Enumeration enumeration = jarFile.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement()
            String entryName = jarEntry.getName()
            ZipEntry zipEntry = new ZipEntry(entryName)
            jarOutputStream.putNextEntry(zipEntry)
            InputStream inputStream = jarFile.getInputStream(jarEntry)
            if (entryName.endsWith(".class") && notRClass(entryName) && notSupport(entryName)) {
                byte[] code = getClassByte(IOUtils.toByteArray(inputStream))
                jarOutputStream.write(code)
                inputStream.close()
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
        }
        jarOutputStream.close()
        fos.close()
        jarFile.close()
        return tempFile
    }

    List<ComponentVisitor> components = new ArrayList<>() // 临时存放visitors  模块
    List<ComponentVisitor> routers = new ArrayList<>() // 临时存放visitors 路由

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        project.logger.error("========== 开始 transform ==========")
        long start = System.currentTimeMillis()
        File jarTargetComponent = null// 要插入的Manager所在的jar包
        def destTarget// 最终的jar对应的inputs

        inputs.each { TransformInput input ->
            // /NeacyComponent/app/build/intermediates/classes/debug
            project.logger.error("------ 开始遍历DireactoryInput ------ " + input.directoryInputs.size())
            input.directoryInputs.each { DirectoryInput directoryInput ->
                def path = directoryInput.file.absolutePath
                project.logger.error("=== Directory Path = " + path)

                if (directoryInput.file.isDirectory()) {
                    directoryInput.file.eachFileRecurse { File file ->
                        project.logger.error("==== file = " + file.absolutePath)
                        if (!file.isDirectory() && notRClass(file.name) && file.name.endsWith(".class")) {
                            boolean isFindSuccess = scanClassFiles(file.bytes)
                            if (isFindSuccess) {// 如果成功找到  那么处理相关的Intent上的数据
                                insertClassFileForDirectory(file)
                            }
                        }
                    }
                }

                def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            // /NeacyComponent/app/build/intermediates/exploded-aar/com.android.support/support-v4/23.0.1/jars/classes.jar
            project.logger.error("------ 开始遍历JarInput ------ " + input.jarInputs.size())
            input.jarInputs.each { JarInput jarInput ->
                def path = jarInput.file.absolutePath
                if (!path.startsWith("android")) {
                    project.logger.error("=== JarInput Path = " + path)
                }
                def jarName = jarInput.name
                project.logger.error("=== jarName = " + jarName)
                def md5Name = DigestUtils.md5Hex(path)
                def tempJarFile = null
                if (path.endsWith(".jar")) {
                    JarFile jarFile = new JarFile(jarInput.file)
                    Enumeration enumeration = jarFile.entries()
                    boolean isFindSuccess = false
                    while (enumeration.hasMoreElements()) {
                        JarEntry jarEntry = enumeration.nextElement()
                        String entryName = jarEntry.getName()
                        if (entryName.endsWith(".class") && notRClass(entryName) && notSupport(entryName)) {
                            InputStream inputStream = jarFile.getInputStream(jarEntry)
                            boolean isFindSuccessTemp = scanClassFiles(IOUtils.toByteArray(inputStream))
                            if (entryName.endsWith(componentConfig.insertClassName)
                                    || entryName.endsWith(componentConfig.routerClassName)) {
                                jarTargetComponent = jarInput.file
                            }
                            if (isFindSuccessTemp) {
                                isFindSuccess = isFindSuccessTemp
                            }
                            inputStream.close()
                        }
                    }
                    if (isFindSuccess) {// 如果说这个jar包存在需要getIntent进行解析的代码
                        tempJarFile = insertClassFileForJar(jarInput.file)
                    }
                }

                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR)
                if (jarTargetComponent == null) {
                    // 如果并不是insertClassName/routerClassName所在的jar 那么直接写入outputs
                    // 如果中间做扫描Intent代码插入的话  那么写入的jar包是tempJarFile
                    FileUtils.copyFile(tempJarFile != null ? tempJarFile : jarInput.file, dest)
                    if (tempJarFile != null) {
                        tempJarFile.delete()
                    }
                } else {
                    destTarget = dest
                }
            }
        }

        if (jarTargetComponent != null) {
            // 将jar包解压后重新打包的路径
            File tempFile = new File(jarTargetComponent.getParent() + File.separator + "component_temp.jar")
            if (tempFile.exists()) {
                tempFile.delete()
            }
            FileOutputStream fos = new FileOutputStream(tempFile)
            JarOutputStream jarOutputStream = new JarOutputStream(fos)

            JarFile jarFile = new JarFile(jarTargetComponent)
            Enumeration enumeration = jarFile.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                jarOutputStream.putNextEntry(zipEntry)
                InputStream inputStream = jarFile.getInputStream(jarEntry)
                if (entryName.endsWith(componentConfig.insertClassName)) {// 插入模块化代码
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ComponentInsertVisitor visitor = new ComponentInsertVisitor(Opcodes.ASM5, classWriter)
                    visitor.setData(components)// 这边第一个参数没有获取到直接传null
                    classReader.accept(visitor, ClassReader.EXPAND_FRAMES)

                    byte[] code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else if (entryName.endsWith(componentConfig.routerClassName)) {// 插入路由代码
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ComponentRouterVisitor visitor = new ComponentRouterVisitor(Opcodes.ASM5, classWriter)
                    visitor.setData(routers)// 这边第一个参数没有获取到直接传null
                    classReader.accept(visitor, ClassReader.EXPAND_FRAMES)

                    byte[] code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {// 不需要处理的类直接放回jar包
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                inputStream.close()
            }
            jarOutputStream.close()
            fos.close()
            jarFile.close()

            if (destTarget != null) {
                FileUtils.copyFile(tempFile, destTarget)
            }
            if (tempFile != null) {// 写成inputs之后就删除临时的文件
                tempFile.delete()
            }
        }
        long costTime = System.currentTimeMillis() - start
        project.logger.error("========== 结束 transform 预计耗时 = $costTime ms ========== ")
    }
}