package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PatchMacMouseFBP extends PatchMac {
    public PatchMacMouseFBP(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        AtomicInteger count = new AtomicInteger();
        for (MethodNode method : classNode.methods) {
            InsnList insnList = method.instructions;
            Iterable<AbstractInsnNode> insnsIter = insnList::iterator;
            Stream<AbstractInsnNode> insns = StreamSupport.stream(insnsIter.spliterator(), false);

            insns.filter(insn -> insn instanceof MethodInsnNode)
                    .map(insn -> (MethodInsnNode) insn)
                    .filter(insn -> insn.owner.equals("org/lwjgl/input/Mouse") && insn.name.equals("setGrabbed"))
                    .forEach(insn -> {
                        count.getAndIncrement();
                        insn.owner = "tv/darkosto/sevpatches/core/hooks/MacMouseHook";
                    });
        }

        return count.get() > 0;
    }

    @Override
    protected byte[] writeClass() {
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
