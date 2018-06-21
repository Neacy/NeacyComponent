package com.component.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
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
    private void scanClassFiles(final byte[] b) {
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
    }

    List<ComponentVisitor> components = new ArrayList<>() // 临时存放visitors  模块
    List<ComponentVisitor> routers = new ArrayList<>() // 临时存放visitors 路由

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        project.logger.error("========== 开始 transform ==========")
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
                            scanClassFiles(file.bytes)
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
                project.logger.error("=== JarInput Path = " + path)
                def jarName = jarInput.name
                project.logger.error("=== jarName = " + jarName)
                def md5Name = DigestUtils.md5Hex(path)
                if (path.endsWith(".jar")) {
                    JarFile jarFile = new JarFile(jarInput.file)
                    Enumeration enumeration = jarFile.entries()
                    while (enumeration.hasMoreElements()) {
                        JarEntry jarEntry = enumeration.nextElement()
                        String entryName = jarEntry.getName()
                        println "==== jarInput class entryName :" + entryName
                        if (entryName.endsWith(".class") && notRClass(entryName) && notSupport(entryName)) {
                            InputStream inputStream = jarFile.getInputStream(jarEntry)
                            scanClassFiles(IOUtils.toByteArray(inputStream))

                            if (entryName.endsWith(componentConfig.insertClassName)
                                    || entryName.endsWith(componentConfig.routerClassName)) {
                                jarTargetComponent = jarInput.file
                            }
                            inputStream.close()
                        }
                    }
                }

                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR)
                if (jarTargetComponent == null) {// 如果并不是insertClassName/routerClassName所在的jar 那么直接写入outputs
                    FileUtils.copyFile(jarInput.file, dest)
                } else {
                    destTarget = dest
                }
            }
        }

        if (jarTargetComponent != null) {
            // 将jar包解压后重新打包的路径
            File tempFile = new File(jarTargetComponent.getParent() + File.separator + "neacy_temp.jar")
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
        project.logger.error("========== 结束 transform  ==========")
    }
}