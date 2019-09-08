package cn.router.wang.tranforms

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import cn.router.wang.utils.GeneratorUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Created to :  
 * @author WANG
 * @date 2019/1/23
 */
class HandleTransform extends Transform {

    @Override
    String getName() {
        return "WeRouter"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        TransformHelper.initClasses.clear()
        TransformHelper.targetFile = null
        System.out.println("==============start==============")
        boolean leftSlash = File.separator == '/'
        transformInvocation.inputs.each { TransformInput transformInput ->
            transformInput.jarInputs.each { JarInput jarInput ->
                //这个是遍历的当前项目下所有的Module的Jar包,除了Application所属的Module下的文件遍历不到.
                def jarName = jarInput.name
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def outFile = transformInvocation.outputProvider.getContentLocation(jarName, jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR)
                scanJar(jarInput.file, outFile)
                //会把每个jar包copy到APP的transforms的对应目录下面.
                FileUtils.copyFile(jarInput.file, outFile)
            }

            transformInput.directoryInputs.each { DirectoryInput directoryInput ->
                //这个是遍历Application所属的Module下的文件.
                //路径为/Users/mc/Documents/AndroidWorkSpace/PluginDemo/app/build/intermediates/classes/debug
                def rootDirFile = directoryInput.file
                String rootDirPath = rootDirFile.absolutePath
                if (!rootDirPath.endsWith(File.separator)) {
                    rootDirPath += File.separator
                }
                //复制jar包到指定的目录app/build/intermediates/classes/debug.
                def outDirFile = transformInvocation.outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                rootDirFile.eachFileRecurse { File file ->
                    //这个路径为上面根目录下面的子文件的全路径 也就是该文件在你电脑上的全路径,这里我们肯定是不需要的,我们需要的是一个.class文件的全限定名.
                    //需要截取除了根路径之外的子目录路径
                    def filePath = file.absolutePath
                    def childPath = filePath.replace(rootDirPath, "")
                    if (!leftSlash) {
                        childPath = childPath.replaceAll("\\\\", "/")
                    }
                    def findClassPackage = TransformHelper.findClassPackage.replace(".","/")
                    if (childPath.startsWith(findClassPackage) && childPath.endsWith(".class")) {
                        if (file.isFile()) {
                            visitClass(new FileInputStream(file))
                        }
                    }
                }
                //copy dir
                FileUtils.copyDirectory(rootDirFile, outDirFile)
            }
        }

        System.out.println("==============initClasses  " + TransformHelper.initClasses.size() +"    targetFile     "+TransformHelper.targetFile)
        if (TransformHelper.targetFile && TransformHelper.initClasses.size() > 0) {
            GeneratorUtils.generatorCode(TransformHelper.targetFile)
            System.out.println("==============end==============")
        }
    }

    private void scanJar(File file, File outFile) {
        JarFile jarFile = new JarFile(file)
        def entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement()
            def jarEntryName = jarEntry.name
            def targetPath = TransformHelper.targetFilePath_replace + ".class"
            def findClassPackage = TransformHelper.findClassPackage.replace(".","/")
            if (jarEntryName == targetPath) {
                System.out.println("==============找到目标文件  " + jarEntryName)
                TransformHelper.targetFile = outFile
            } else if (jarEntryName.startsWith(findClassPackage)) {
                def is = jarFile.getInputStream(jarEntry)
                visitClass(is)
            }
        }

    }

    private void visitClass(InputStream inputStream) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        RouterClassVisit rcv = new RouterClassVisit(Opcodes.ASM5, cw)
        cr.accept(rcv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }

    private class RouterClassVisit extends ClassVisitor {

        RouterClassVisit(int i, ClassVisitor classVisitor) {
            super(i, classVisitor)
        }
        //(jdk的版本号,类限制符,类的全限定名,签名,父类,实现的接口)
        @Override
        void visit(int i, int i1, String s, String s1, String s2, String[] strings) {
            super.visit(i, i1, s, s1, s2, strings)
            strings.each { String str ->
                def targetP = TransformHelper.method_provider.replace(".", "/")
                def targetR = TransformHelper.method_root.replace(".", "/")
                if (targetP == str || targetR == str) {
                    TransformHelper.initClasses.add(s)
                    System.out.println("==============找到了目标类  " + s)
                }
            }
        }
    }
}
