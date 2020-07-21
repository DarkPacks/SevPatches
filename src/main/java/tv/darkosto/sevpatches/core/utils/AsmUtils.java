package tv.darkosto.sevpatches.core.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public class AsmUtils {
    public static MethodNode findMethod(ClassNode classNode, String methodName) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(methodName)) return methodNode;
        }

        return null;
    }

    public static AnnotationNode findAnnotation(MethodNode targetMethod, String annotationDesc) {
        for (AnnotationNode annotationNode : targetMethod.visibleAnnotations) {
            if (annotationNode.desc.equals(annotationDesc)) return annotationNode;
        }

        return null;
    }

    public static InsnNode findReturn(MethodNode targetMethod) {
        for (ListIterator<AbstractInsnNode> it = targetMethod.instructions.iterator(); it.hasNext(); ) {
            AbstractInsnNode insnNode = it.next();

            if (insnNode.getOpcode() == Opcodes.RETURN) return (InsnNode) insnNode;
        }
        return null;
    }
}
