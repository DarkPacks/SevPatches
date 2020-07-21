package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;

public abstract class Patch {
    ClassNode classNode;
    byte[] inputClassBytes;

    public Patch(byte[] inputClass) {
        inputClassBytes = inputClass;
        ClassReader classReader = new ClassReader(inputClass);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
    }

    public byte[] apply() {
        if (patch()) {
            return writeClass();
        } else {
            SevPatchesLoadingPlugin.LOGGER.error("{} failed", this.getClass().getSimpleName());
            return inputClassBytes;
        }
    }

    protected byte[] writeClass() {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    protected abstract boolean patch();
}
