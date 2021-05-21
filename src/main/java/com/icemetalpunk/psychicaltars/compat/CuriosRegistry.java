package com.icemetalpunk.psychicaltars.compat;

import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import com.icemetalpunk.psychicaltars.PsychicAltars;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;

public class CuriosRegistry implements ICuriosCompat {
	public CuriosRegistry() {
	};

	@Override
	public void registerCurioSlots() {
		InterModComms.sendTo(PsychicAltars.MODID, "curios", SlotTypeMessage.REGISTER_TYPE,
				() -> new SlotTypeMessage.Builder("charm").build());
	}

	@Override
	public Optional<ImmutableTriple<String, Integer, ItemStack>> getCurioFromItem(Item item, LivingEntity entity) {
		return CuriosApi.getCuriosHelper().findEquippedCurio(item, entity);
	}

	@Override
	public Optional<ImmutableTriple<String, Integer, ItemStack>> getCurioFromItem(Predicate<ItemStack> predicate,
			LivingEntity entity) {
		return CuriosApi.getCuriosHelper().findEquippedCurio(predicate, entity);
	}
}