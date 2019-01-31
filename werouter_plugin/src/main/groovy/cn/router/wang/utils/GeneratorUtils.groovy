package cn.router.wang.utils

import cn.router.wang.tranforms.TransformHelper
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class GeneratorUtils {

    static void generatorCode(File targetFile) {
        def targetPath = targetFile.absolutePath
        System.out.println("-----------targetFile " + targetPath)
        //重写一个jar文件不能再里面直接写,需要复制一份写完,然后把之前的那份删掉.
        //第一步先创建父文件夹
        def optFile = new File(targetFile.getParent(), "WeRouter")
        if (optFile.exists()) {
            optFile.delete()
        }
        def jarFile = new JarFile(targetFile)
        //第二步需要穿件一个输出流 也就是写入
        JarOutputStream jos = new JarOutputStream(new FileOutputStream(optFile))
        Enumeration enumeration = jarFile.entries()
        //遍历去查找我们要的文件,找到之后我们就去处理,不符合的就原样输入
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement()
            def jarname = jarEntry.name
            //JarEntry 为ZipEntry的子类,新的jar文件需要创建一个新的JarEntry对象
            ZipEntry zipEntry = new ZipEntry(jarname)
            //每一个Jar包都要有一个JarEntry对象
            jos.putNextEntry(zipEntry)
            //获取JarFile的输入流
            def ins = jarFile.getInputStream(jarEntry)
            //找到我目标文件
            if (jarname.startsWith(TransformHelper.targetFilePath_replace)) {
                //获取输入流,开始遍历jar文件
                byte[] bytes = visitClassForGenera(ins)
                jos.write(bytes)
            } else {
                jos.write(IOUtils.toByteArray(ins))
            }
            ins.close()
            jos.closeEntry()
        }
        jos.close()
        jarFile.close()
        if (targetFile.exists()) {
            targetFile.delete()
        }
        optFile.renameTo(targetFile)
    }

    private static byte[] visitClassForGenera(InputStream inputStream) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        GeneratorClassVisit gcv = new GeneratorClassVisit(Opcodes.ASM5, cw)
        cr.accept(gcv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    private static class GeneratorClassVisit extends ClassVisitor {

        GeneratorClassVisit(int i, ClassVisitor classVisitor) {
            super(i, classVisitor)
        }

        @Override
        void visit(int i, int i1, String s, String s1, String s2, String[] strings) {
            super.visit(i, i1, s, s1, s2, strings)
        }

        //(访问权限,方法名,表达式,签名,抛出的异常)
        @Override
        MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
            MethodVisitor methodVisitor = super.visitMethod(i, s, s1, s2, strings)
            if (s == TransformHelper.findMethodName) {
                methodVisitor = new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                    @Override
                    void visitInsn(int opcode) {
                        //再方法返回之前操作
                        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
                            TransformHelper.initClasses.each { String classname ->
                                def clz = classname.replace("/", ".")
                                System.out.println("-------find  method  name  is   " + clz)
                                mv.visitLdcInsn(clz)
                                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                        TransformHelper.targetFilePath_replace,
                                        TransformHelper.generaMethodName,
                                        "(Ljava/lang/String;)V",
                                        false)
                            }
                        }

                        super.visitInsn(opcode)
                    }

                    @Override
                    void visitMaxs(int maxStack, int maxLocals) {
                        super.visitMaxs(maxStack + 4, maxLocals)
                    }
                }
            }
            return methodVisitor
        }
    }

}