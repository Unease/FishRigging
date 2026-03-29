package com.minecrafttas.fishrigging.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecrafttas.fishrigging.FishRigging;

import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.math.MathHelper;

@Mixin(EntityFishHook.class)
public class MixinEntityFishHook {

	//# 1.12.2
//$$	@Shadow
//$$	private int lureSpeed;
	//# 1.11.2
	@Shadow
	private int f_16491628;
	//# end
	
	@Redirect(method = "catchingFish", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F", ordinal = 0))
	private float redirectRaining(Random rand) {
		
		if (FishRigging.isTASModLoaded) {
			return 0;
		}
		
		return rand.nextFloat();
	}
	
	@Redirect(method = "catchingFish", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F", ordinal = 1))
	private float redirectOpenSky(Random rand) {
		
		if (FishRigging.isTASModLoaded) {
			return 1;
		}
		
		return rand.nextFloat();
	}
	
	@Redirect(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;getInt(Ljava/util/Random;II)I", ordinal = 1))
	private int redirectMinWait(Random rand, int min, int max) {
		
		if (FishRigging.isTASModLoaded) {
			return 20;
		}
		return MathHelper.getInt(rand, 20, 80);
	}
	
	@Redirect(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;getInt(Ljava/util/Random;II)I", ordinal = 2))
	private int redirectFishApproach(Random rand, int min, int max) {
		
		//# 1.12.2
//$$		int lureSpeed = this.lureSpeed;
		//# 1.11.2
		int lureSpeed = this.f_16491628;
		//# end
		
		if (FishRigging.isTASModLoaded) {
			
			if (lureSpeed >= 1) {
				return lureSpeed * 100 + 1;
			}
			
			return 100;
			
		}

		return MathHelper.getInt(rand, 100, 600);
	}
}
