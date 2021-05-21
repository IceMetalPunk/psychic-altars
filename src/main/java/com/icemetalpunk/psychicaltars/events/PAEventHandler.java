package com.icemetalpunk.psychicaltars.events;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import com.icemetalpunk.psychicaltars.helpers.FakePlayerHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class PAEventHandler {
	private static final Field loveCauseField = ObfuscationReflectionHelper.findField(AnimalEntity.class, "field_146084_br");

	@SubscribeEvent
	public void babySpawnHandler(final BabyEntitySpawnEvent event) {
		System.out.println("In handler!");
		MobEntity parentA = event.getParentA();
		MobEntity parentB = event.getParentB();
		MobEntity child = event.getChild();
		if (!(parentA instanceof AnimalEntity) || !(parentB instanceof AnimalEntity)
				|| !(child instanceof AnimalEntity)) {
			return;
		}
		AnimalEntity animalParentA = (AnimalEntity) parentA;
		AnimalEntity animalParentB = (AnimalEntity) parentB;
		AnimalEntity animalChild = (AnimalEntity) child;
		ServerWorld world = animalParentA.getServer().overworld();
		FakePlayer nullBreeder = FakePlayerHelper.getOrCreate(world, "Breeder 0");
		System.out.println("Is Animal in world " + world);
		UUID loveUUID;
		try {
			loveUUID = (UUID) loveCauseField.get(animalParentA);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("UUID: " + loveUUID);
		Optional<FakePlayer> optionalPlayer = FakePlayerHelper.getByUUID(world, loveUUID);
		if (!optionalPlayer.isPresent()) {
			System.out.println("Not present");
			return;
		}
		FakePlayer player = optionalPlayer.get();
		System.out.println("Player: " + player.getName());
		CompoundNBT nbtTag = new CompoundNBT();
		animalChild.save(nbtTag);
		nbtTag.remove("UUID");
		EntityType<?> type = animalChild.getType();
		for (int i = 1; i <= 4; ++i) {
			if (player == FakePlayerHelper.getOrCreate(world, "Breeder " + i)) {
				System.out.println("Is Breeder " + i);
				animalParentA.setInLove(nullBreeder);
				animalParentA.resetLove();
				animalParentB.setInLove(nullBreeder);
				animalParentB.resetLove();
				for (int j = 0; j < i; ++i) {
					Optional<Entity> babyClone = EntityType.create(nbtTag.copy(), world);
					babyClone.ifPresent(baby -> world.addFreshEntity(baby));
				}
				break;
			}
		}
	}
}
