package tv.darkosto.sevpatches.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.ListIterator;

public class SevPatchesTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
            case "tehnut.harvest.ReplantHandlers":
                return this.harvestTransform(basicClass);
            case "mcjty.incontrol.ForgeEventHandlers":
                return this.inControlTransform(basicClass);
            case "blusunrize.immersiveengineering.common.blocks.metal.TileEntityMetalPress":
                return this.metalPressTransform(basicClass);
            case "hellfirepvp.astralsorcery.common.enchantment.amulet.EnchantmentUpgradeHelper":
                return this.amuletTransform(basicClass);
            case "micdoodle8.mods.galacticraft.core.blocks.BlockBasic":
                return this.galacticraftBlockTransform(basicClass);
            default:
                return basicClass;
        }
    }

    private void setEventSubPriority(ClassNode input, String targetMethod, String priority) {
        for (MethodNode methodNode : input.methods) {
            if (!methodNode.name.equals(targetMethod)) continue;
            SevPatchesLoadingPlugin.LOGGER.info("Altering priority of handler: {} to: {}", targetMethod, priority);
            for (AnnotationNode annotationNode : methodNode.visibleAnnotations) {
                if (!annotationNode.desc.equals("Lnet/minecraftforge/fml/common/eventhandler/SubscribeEvent;"))
                    continue;

                if (annotationNode.values.contains("priority"))
                    ((String[]) annotationNode.values.get(annotationNode.values.indexOf("priority") + 1))[1] = priority;
                else
                    annotationNode.values.addAll(Arrays.asList(
                            "priority",
                            new String[]{"Lnet/minecraftforge/fml/common/eventhandler/EventPriority;", priority}
                    ));
            }
        }
    }

    /**
     * DarkPacks/SevTech-Ages#3522
     */
    private byte[] galacticraftBlockTransform(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (!methodNode.name.equals("getPickBlock")) continue;
            for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
                AbstractInsnNode insnNode = it.next();
                if (insnNode instanceof MethodInsnNode && ((MethodInsnNode) insnNode).name.equals(SevPatchesLoadingPlugin.GET_BLOCK_STATE)) {
                    methodNode.instructions.remove(insnNode.getPrevious());
                    ((VarInsnNode) insnNode.getPrevious()).var = 1;
                    methodNode.instructions.remove(insnNode);
                }
            }
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    /**
     * DarkPacks/SevTech-Ages#4091
     */
    private byte[] amuletTransform(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (!methodNode.name.equals("modifyEnchantmentTags")) continue;

            for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
                AbstractInsnNode insnNode = it.next();

                if (!(insnNode instanceof LdcInsnNode) || !((LdcInsnNode) insnNode).cst.equals("id")) continue;

                boolean foundTarget = false;
                while (!foundTarget) {
                    do {
                        insnNode = it.next();
                    } while (!(insnNode instanceof MethodInsnNode));

                    MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
                    if (methodInsnNode.name.equals(SevPatchesLoadingPlugin.GET_SHORT)) {
                        methodInsnNode.name = SevPatchesLoadingPlugin.GET_INT;
                        methodInsnNode.desc = "(Ljava/lang/String;)I";

                        foundTarget = true;
                    } else if (methodInsnNode.name.equals(SevPatchesLoadingPlugin.SET_SHORT)) {
                        methodInsnNode.name = SevPatchesLoadingPlugin.SET_INT;
                        methodInsnNode.desc = "(Ljava/lang/String;I)V";

                        methodNode.instructions.remove(methodInsnNode.getPrevious()); // remove I2S
                        foundTarget = true;
                    }
                }
            }
        }

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    /**
     * DarkPacks/SevTech-Ages#3732
     */
    private byte[] metalPressTransform(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (!methodNode.name.equals("onEntityCollision")) continue;

            int targetInsnNodeIndex = -1;
            for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
                AbstractInsnNode insnNode = it.next();
                if (insnNode instanceof TypeInsnNode) {
                    TypeInsnNode typeInsnNode = (TypeInsnNode) insnNode;
                    if (typeInsnNode.getOpcode() == Opcodes.INSTANCEOF
                            && typeInsnNode.desc.equals("net/minecraft/entity/item/EntityItem")) {
                        targetInsnNodeIndex = methodNode.instructions.indexOf(insnNode);
                    }
                }
            }

            if (targetInsnNodeIndex == -1) {
                SevPatchesLoadingPlugin.LOGGER.warn("No location matching the target");
                continue;
            }

            LabelNode jumpLocation = null;
            for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
                AbstractInsnNode insnNode = it.next();
                if (insnNode instanceof LabelNode) {
                    if (insnNode.getNext().getNext().getNext().getOpcode() == Opcodes.RETURN) {
                        jumpLocation = (LabelNode) insnNode;
                        break;
                    }
                }
            }
            if (jumpLocation == null) {
                SevPatchesLoadingPlugin.LOGGER.warn("Couldn't find jump location");
                continue;
            }

            InsnList patch = new InsnList();
            patch.add(new VarInsnNode(Opcodes.ALOAD, 2));
            patch.add(new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Object",
                    "getClass",
                    "()Ljava/lang/Class;",
                    false
            ));
            patch.add(new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Class",
                    "getName",
                    "()Ljava/lang/String;",
                    false
            ));
            patch.add(new LdcInsnNode("net.minecraft.entity.item.EntityItem"));
            patch.add(new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/String",
                    "equals",
                    "(Ljava/lang/Object;)Z",
                    false
            ));
            patch.add(new JumpInsnNode(
                    Opcodes.IFNE,
                    jumpLocation
            ));


            methodNode.instructions.insert(
                    methodNode.instructions.get(targetInsnNodeIndex).getNext(),
                    patch
            );
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    /**
     * DarkPacks/SevTech-Ages#3847
     */
    private byte[] inControlTransform(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        setEventSubPriority(classNode, "onLivingDrops", "LOW");

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    /**
     * DarkPacks/SevTech-Ages#3829
     */
    private byte[] harvestTransform(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (!methodNode.name.equals("lambda$static$0")) continue;
            InsnList eventFire = new InsnList();
            Label l = new Label();
            LabelNode ln = new LabelNode(l);
            eventFire.add(ln);
            eventFire.add(new LineNumberNode(19, ln));
            eventFire.add(new VarInsnNode(Opcodes.ALOAD, 7));
            eventFire.add(new VarInsnNode(Opcodes.ALOAD, 0));
            eventFire.add(new VarInsnNode(Opcodes.ALOAD, 1));
            eventFire.add(new VarInsnNode(Opcodes.ALOAD, 2));
            eventFire.add(new InsnNode(Opcodes.ICONST_0));
            eventFire.add(new InsnNode(Opcodes.FCONST_1));
            eventFire.add(new InsnNode(Opcodes.ICONST_0));
            eventFire.add(new VarInsnNode(Opcodes.ALOAD, 3));
            eventFire.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "net/minecraftforge/event/ForgeEventFactory",
                    "fireBlockHarvesting",
                    "(" +
                            "Ljava/util/List;" +
                            "Lnet/minecraft/world/World;" +
                            "Lnet/minecraft/util/math/BlockPos;" +
                            "Lnet/minecraft/block/state/IBlockState;" +
                            "IFZ" +
                            "Lnet/minecraft/entity/player/EntityPlayer;" +
                            ")F",
                    false
            ));
            eventFire.add(new InsnNode(Opcodes.POP));

            for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
                AbstractInsnNode insnNode = it.next();
                if (insnNode.getNext() instanceof LineNumberNode) {
                    LineNumberNode lnn = (LineNumberNode) insnNode.getNext();
                    if (lnn.line == 19) {
                        do {
                            methodNode.instructions.remove(insnNode);
                            insnNode = it.next();
                        } while (!(insnNode instanceof LabelNode));
                    }
                    if (lnn.line == 44) {
                        methodNode.instructions.insertBefore(insnNode, eventFire);
                    }
                }
            }
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
