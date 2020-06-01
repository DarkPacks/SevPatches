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
            case "net.minecraft.world.WorldEntitySpawner":
                return this.spawnChunkSpawning(basicClass);
            case "tehnut.harvest.ReplantHandlers":
                return this.harvestTransform(basicClass);
            case "mcjty.incontrol.ForgeEventHandlers":
                return this.inControlTransform(basicClass);
            case "blusunrize.immersiveengineering.common.blocks.metal.TileEntityMetalPress":
                return this.metalPressTransform(basicClass);
            case "hellfirepvp.astralsorcery.common.enchantment.amulet.EnchantmentUpgradeHelper":
                return this.amuletTransform(basicClass);
            case "hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectBootes":
                return this.bootesRealDrops(basicClass);
            case "micdoodle8.mods.galacticraft.core.blocks.BlockBasic":
                return this.galacticraftBlockTransform(basicClass);
            case "com.tmtravlr.jaff.JAFFEventHandler":
                return this.unnecessaryLagRemover(basicClass);
            case "com.tmtravlr.jaff.JAFFMod":
                return this.fishLiveInWater(basicClass);
            case "com.tmtravlr.jaff.entities.EntityFish":
                return this.fishAreFish(basicClass);
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
     * Make it possible for spawning to occur in the spawn chunks
     */
    private byte[] spawnChunkSpawning(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        MethodNode findChunksForSpawning = null;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(SevPatchesLoadingPlugin.FIND_CHUNKS_FOR_SPAWNING) && methodNode.desc.equals(SevPatchesLoadingPlugin.FIND_CHUNKS_FOR_SPAWNING_DESC)) {
                findChunksForSpawning = methodNode;
            }
        }

        if (findChunksForSpawning == null) {
            SevPatchesLoadingPlugin.LOGGER.warn("Couldn't find target method node: WorldEntitySpawner#findChunksForSpawning");
            return basicClass;
        }

        MethodInsnNode distanceSq = null;
        for (ListIterator<AbstractInsnNode> it = findChunksForSpawning.instructions.iterator(); it.hasNext(); ) {
            AbstractInsnNode insnNode = it.next();
            if (insnNode instanceof MethodInsnNode) {
                MethodInsnNode mInsnNode = (MethodInsnNode) insnNode;
                if (mInsnNode.name.equals(SevPatchesLoadingPlugin.VEC_3I_DISTANCE_SQ) && mInsnNode.desc.equals(SevPatchesLoadingPlugin.VEC_3I_DISTANCE_SQ_DESC)) {
                    distanceSq = (MethodInsnNode) insnNode;
                }
            }
        }

        if (distanceSq == null) {
            SevPatchesLoadingPlugin.LOGGER.warn("Couldn't find target method invocation: Vec3i#distanceSq");
            return basicClass;
        }

        MethodInsnNode distanceSqRedirect = new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "tv/darkosto/sevpatches/core/hooks/SpawnHook",
                "distanceSqRedirect",
                "(Lnet/minecraft/util/math/BlockPos;DDD)D",
                false
        );
        findChunksForSpawning.instructions.insert(distanceSq, distanceSqRedirect);
        findChunksForSpawning.instructions.remove(distanceSq);

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    /**
     * <p>Make JAFF fish behave like fish</p>
     * <p>The JAFFA patch</p>
     */
    private byte[] unnecessaryLagRemover(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (!methodNode.name.equals("onWorldTick")) continue;
            for (AnnotationNode annotationNode : methodNode.visibleAnnotations) {
                if (!annotationNode.desc.equals("Lnet/minecraftforge/fml/common/eventhandler/SubscribeEvent;"))
                    continue;

                methodNode.visibleAnnotations.remove(annotationNode);
                SevPatchesLoadingPlugin.LOGGER.info("Disabling custom spawn logic in JAFF");
                break;
            }
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private byte[] fishLiveInWater(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        MethodNode preInit = null;
        MethodNode postInit = null;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("preInit")) preInit = methodNode;
            if (methodNode.name.equals("postInit")) postInit = methodNode;
        }

        if (preInit == null || postInit == null) {
            SevPatchesLoadingPlugin.LOGGER.warn("Failed to find JAFF init methods");
            return basicClass;
        }

        InsnList callRegisterPlacement = new InsnList();
        callRegisterPlacement.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "tv/darkosto/sevpatches/core/hooks/FishHook",
                "registerPlacement",
                "()V",
                false
        ));

        InsnNode preInitReturn = null;
        for (ListIterator<AbstractInsnNode> it = preInit.instructions.iterator(); it.hasNext(); ) {
            AbstractInsnNode insnNode = it.next();

            if (insnNode.getOpcode() == Opcodes.RETURN) preInitReturn = (InsnNode) insnNode;
        }

        if (preInitReturn != null)
            preInit.instructions.insertBefore(preInitReturn, callRegisterPlacement);

        InsnList callRegisterSpawns = new InsnList();
        callRegisterSpawns.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "tv/darkosto/sevpatches/core/hooks/FishHook",
                "registerSpawns",
                "()V",
                false
        ));

        InsnNode postInitReturn = null;
        for (ListIterator<AbstractInsnNode> it = postInit.instructions.iterator(); it.hasNext(); ) {
            AbstractInsnNode insnNode = it.next();

            if (insnNode.getOpcode() == Opcodes.RETURN) postInitReturn = (InsnNode) insnNode;
        }

        if (postInitReturn != null)
            postInit.instructions.insertBefore(postInitReturn, callRegisterSpawns);

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private byte[] fishAreFish(byte[] basicClass) {
        SevPatchesLoadingPlugin.LOGGER.info("JAFFA patch in progress");

        ClassReader classReader = new ClassReader(basicClass);

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        /*
        public boolean isNotColliding() {
            this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this);
        }
         */
        MethodNode isFishNotColliding = new MethodNode(
                Opcodes.ACC_PUBLIC,
                SevPatchesLoadingPlugin.ENTITY_IS_NOT_COLLIDING,
                "()Z",
                null,
                null
        );
        InsnList fishyInsns = isFishNotColliding.instructions;
        fishyInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
        fishyInsns.add(new FieldInsnNode(
                Opcodes.GETFIELD,
                "com/tmtravlr/jaff/entities/EntityFish",
                SevPatchesLoadingPlugin.ENTITY_WORLD,
                "Lnet/minecraft/world/World;"
        ));
        fishyInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
        fishyInsns.add(new MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "com/tmtravlr/jaff/entities/EntityFish",
                SevPatchesLoadingPlugin.GET_ENTITY_BOUNDING_BOX,
                "()Lnet/minecraft/util/math/AxisAlignedBB;",
                false
        ));
        fishyInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
        fishyInsns.add(new MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "net/minecraft/world/World",
                SevPatchesLoadingPlugin.CHECK_NO_ENTITY_COLLISION,
                "(Lnet/minecraft/util/math/AxisAlignedBB;Lnet/minecraft/entity/Entity;)Z",
                false
        ));
        fishyInsns.add(new InsnNode(Opcodes.IRETURN));
        classNode.methods.add(isFishNotColliding);
        SevPatchesLoadingPlugin.LOGGER.info("JAFFA patch: implemented isNotColliding");

        for (MethodNode methodNode : classNode.methods) {
            if (!methodNode.name.equals(SevPatchesLoadingPlugin.ENTITY_GET_CAN_SPAWN_HERE)) continue;
            InsnList replacement = new InsnList();
            replacement.add(new InsnNode(Opcodes.ICONST_1));
            replacement.add(new InsnNode(Opcodes.IRETURN));  // return true
            methodNode.instructions = replacement;
            SevPatchesLoadingPlugin.LOGGER.info("JAFFA patch: getCanSpawnHere now always true");
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
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
     * Less cheaty Bootes ritual
     */
    private byte[] bootesRealDrops(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        MethodNode playEffectMN = null;
        for (MethodNode methodNode : classNode.methods) {
            if (!methodNode.name.equals("playEffect")) continue;
            playEffectMN = methodNode;
            break;
        }

        if (playEffectMN == null) {
            SevPatchesLoadingPlugin.LOGGER.warn("Couldn't find target method node: CEffectBootes#playEffect");
            return basicClass;
        }

        MethodInsnNode insertionPoint = null;
        boolean foundEnd = false;

        for (ListIterator<AbstractInsnNode> it = playEffectMN.instructions.iterator(); it.hasNext(); ) {
            AbstractInsnNode insnNode = it.next();
            if (!(insnNode instanceof MethodInsnNode)) continue;
            if (!((MethodInsnNode) insnNode).name.equals("getHerdingDropsTick")) continue;
            insertionPoint = (MethodInsnNode) insnNode;
            while (it.hasNext()) {
                if (insnNode.getNext() instanceof LabelNode && insnNode.getNext().getNext() instanceof LineNumberNode && ((LineNumberNode) insnNode.getNext().getNext()).line == 78) {
                    foundEnd = true;
                    break;
                }
                playEffectMN.instructions.remove(insnNode.getNext());
            }
            break;
        }

        if (insertionPoint == null || !foundEnd) {
            SevPatchesLoadingPlugin.LOGGER.warn("Could not find target instruction: INVOKE getHerdingDropsTick, or failed to find end. Skipping patch.");
            return basicClass;
        }

        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 9));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 6));
        insnList.add(new FieldInsnNode(
                Opcodes.GETSTATIC,
                "hellfirepvp/astralsorcery/common/constellation/effect/aoe/CEffectBootes",
                "rand",
                "Ljava/util/Random;"
        ));
        insnList.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "tv/darkosto/sevpatches/core/hooks/BootesHook",
                "handleBootesDrops",
                "(Ljava/util/List;Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;ZLjava/util/Random;)Z",
                false
        ));
        insnList.add(new VarInsnNode(Opcodes.ISTORE, 6));

        playEffectMN.instructions.insert(insertionPoint, insnList);

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
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
