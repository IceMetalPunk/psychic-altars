package com.icemetalpunk.psychicaltars.compat;

import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CuriosStub implements ICuriosCompat {
	public CuriosStub() {
	}

	@Override
	public void registerCurioSlots() {
	}

	@Override
	public Optional<ImmutableTriple<String, Integer, ItemStack>> getCurioFromItem(Item item, LivingEntity entity) {
		return Optional.empty();
	}

	@Override
	public Optional<ImmutableTriple<String, Integer, ItemStack>> getCurioFromItem(Predicate<ItemStack> predicate,
			LivingEntity entity) {
		return Optional.empty();
	}
}
