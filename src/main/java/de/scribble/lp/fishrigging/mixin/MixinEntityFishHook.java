package de.scribble.lp.fishrigging.mixin;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import de.scribble.lp.fishrigging.FishManipEvents;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

@Mixin(EntityFishHook.class)
public abstract class MixinEntityFishHook {
	@Redirect(method = "catchingFish", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F", ordinal = 0))
	private float isRainingAlwaysIncrementRedirect(Random random) {
		return 0; // Force nextFloat < .25
	}

	@Redirect(method = "catchingFish", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F", ordinal = 1))
	private float noOpenSkyNeverDecrementRedirect(Random random) {
		return 1.0F; // Force nextFloat > 0.5
	}

	@Redirect(method = "handleHookRetraction", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/loot/LootTable;generateLootForPools(Ljava/util/Random;Lnet/minecraft/world/storage/loot/LootContext;)Ljava/util/List;"))
	private List<ItemStack> redirectGenerateLoot(LootTable lootTable, Random rand, LootContext context) {
		if (FishManipEvents.fishrigger.isActive()) {
			return Collections.singletonList(FishManipEvents.fishrigger.getItemFromTop());
		} else {
			return lootTable.generateLootForPools(rand, context);
		}
	}

	@Redirect(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;getInt(Ljava/util/Random;II)I", ordinal = 1))
	private int fastestFishApproachRedirect(Random rand, int min, int max) {
		if (FishManipEvents.fishrigger.isActive()) {
			return 20;
		} else {
			return MathHelper.getInt(rand, min, max);
		}
	}

	@Shadow
	private int field_191519_ax;

	@Redirect(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;getInt(Ljava/util/Random;II)I", ordinal = 2))
	private int fastestFishAppearRedirect(Random rand, int min, int max) {
		if (FishManipEvents.fishrigger.isActive()) {
			if (field_191519_ax >= 1) {
				return field_191519_ax * 20 * 5 + 1;
			}
			return 100;
		}
		return MathHelper.getInt(rand, min, max);
	}
}