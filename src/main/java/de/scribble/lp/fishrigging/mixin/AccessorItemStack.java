package de.scribble.lp.fishrigging.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@Mixin(ItemStack.class)
public interface AccessorItemStack {

	@Accessor("stackTagCompound")
	public void setStackTagCompund(NBTTagCompound compound);
}
