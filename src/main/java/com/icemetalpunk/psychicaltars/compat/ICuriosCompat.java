package com.icemetalpunk.psychicaltars.compat;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ICuriosCompat {
	public void registerCurioSlots();

	public Optional<ImmutableTriple<String, Integer, ItemStack>> getCurioFromItem(Item item,
			@Nonnull LivingEntity entity);

	public Optional<ImmutableTriple<String, Integer, ItemStack>> getCurioFromItem(Predicate<ItemStack> predicate,
			@Nonnull LivingEntity entity);
}
