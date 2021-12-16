package com.nxg.asm;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ASMTest {

    @Test
    public void test() {

        //1、准备待分析的class
        FileInputStream fis;
        try {
            fis = new FileInputStream("/home/work/AndroidStudioProjects/AndroidDevelopmentPractices/AndroidASMSample/app/src/test/java/com/nxg/asm/RoomOpenHelper.class");
            //2、执行分析与插桩
            //class字节码的读取与分析引擎
            ClassReader cr = new ClassReader(fis);
            // 写出器 COMPUTE_FRAMES 自动计算所有的内容，后续操作更简单
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            //分析，处理结果写入cw EXPAND_FRAMES：栈图以扩展格式进行访问
            cr.accept(new ClassAdapterVisitor(cw), ClassReader.EXPAND_FRAMES);


            //3、获得结果并输出
            byte[] newClassBytes = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream
                    ("/home/work/AndroidStudioProjects/AndroidDevelopmentPractices/AndroidASMSample/app/src/test/java/com/nxg/asm/DstRoomOpenHelper.class");
            fos.write(newClassBytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static class ClassAdapterVisitor extends ClassVisitor {

        public ClassAdapterVisitor(ClassVisitor cv) {
            super(Opcodes.ASM7, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                                         String[] exceptions) {
            System.out.println("方法:" + name + " 签名:" + desc);
            if ("onDowngrade".equals(name)) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                return new MethodAdapterVisitor(api, mv, access, name, desc);

            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    public static class MethodAdapterVisitor extends AdviceAdapter {

        protected MethodAdapterVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
        }

        /**
         * 访问方法指令,方法指令是调用方法的指令。
         * 参数：
         * opcode – 要访问的类型指令的操作码。 此操作码是 INVOKEVIRTUAL、INVOKESPECIAL、INVOKESTATIC 或 INVOKEINTERFACE。
         * owner – 方法所有者类的内部名称（请参阅 Type.getInternalName()）。
         * name - 方法的名称。
         * 描述符 – 方法的描述符（请参阅类型）。
         * isInterface – 如果方法的所有者类是接口。
         */
        @Override
        public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
            System.out.println("opcodeAndSource:" + opcodeAndSource + ", owner:" + owner + ", name:" + name + ", descriptor:" + descriptor);
            //移除onUpgrade方法调用
            if (opcodeAndSource == INVOKEVIRTUAL && "onUpgrade".equals(name)) {
                System.out.println("updateIdentity ------------>");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "androidx/room/RoomOpenHelper", "updateIdentity", "(Landroidx/sqlite/db/SupportSQLiteDatabase;)V", false);
                return;
            }
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);


        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();

        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);

        }
    }
}
