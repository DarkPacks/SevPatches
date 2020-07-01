# SevPatches
A small coremod to patch some longstanding issues in SevTech Ages that either could not be resolved in another fashion,
or which have not been resolved in a timely manner.

## Patches
#### [DarkPacks/SevTech-Ages#3829](https://www.github.com/DarkPacks/SevTech-Ages/issues/3829)
Patches SimpleHarvest. Firing of HarvestDropsEvent moved to after the point at which the decision is taken to harvest.

Without this patch, SimpleHarvest fires the HarvestDropsEvent before deciding if it intends to break the block, allowing
mods such as AstralSorcery or IndustrialForegoing to consume the drops array before deciding not to take any action.

#### [DarkPacks/SevTech-Ages#3847](https://www.github.com/DarkPacks/SevTech-Ages/issues/3847)
Patches InControl. Changes the priority of InControl's LivingDropsEvent handler to LOW (was LOWEST). Whilst this change
can't be made to InControl due to compatibility issues in other contexts, no compatibility issues have yet been found in
SevTech. 

This allows mods which wish to consume the drops array to do so after InControl has made the desired changes.

#### [DarkPacks/SevTech-Ages#4179](https://www.github.com/DarkPacks/SevTech-Ages/issues/4179)
Patches Realistic Item Drops. Changes the order of operations when replacing the vanilla item entity. The realistic item
drop is now constructed at the same time as the vanilla item entity is removed from the world, rather than in the 
previous tick. This means that changes are reflected in the resulting drop.

#### [DarkPacks/SevTech-Ages#4091](https://www.github.com/DarkPacks/SevTech-Ages/issues/4091)
Patches Astral Sorcery and Minecraft. There are two components to this patch. The first alters Astral's hooks to use
integer enchantment IDs. The second component reapplies Astral's ASM patches using Mixins as otherwise they are
overwritten by JEID.

This restores functionality to the Resplendent Prism, though in an ideal world the first component of this patch would
be handled by JEID, whilst the second would be unnecessary if JEID didn't use an overwrite mixin.

#### [DarkPacks/SevTech-Ages#3522](https://www.github.com/DarkPacks/SevTech-Ages/issues/3522)
Patches Galacticraft Core. In the method BlockBasic#getPickBlock, Galacticraft retrieves the blockstate from the world
rather than using the state passed in to the method. This leaves it incompatible with Scannable which does not pass in
the BlockPos when calling Block#getPickBlock. This patch simply removes the call to World#getBlockState, replacing it
with the state from the argument.

#### Just A Few Fish Augmentation (JAFFA)
A set of three patches to JAFF which together serve to implement vanilla spawning mechanics.
  * Removes JAFF's own spawn loop
  * Register vanilla spawns and placement types
  * Implement EntityFish#isNotColliding, overriding EntityAnimal's implementation which prevents spawning in water.

#### Less cheaty Bootes Ritaul
In SevTech, a number of mods make changes to animal drops using the LivingDropsEvent, though by default the Bootes
ritual does not fire this event. This patches Astral Sorcery's CEffectBootes so that LivingDropsEvent is fired and the
mutated drops array is used instead of the raw loot table.
