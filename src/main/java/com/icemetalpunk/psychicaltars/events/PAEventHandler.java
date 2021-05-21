package com.icemetalpunk.psychicaltars.events;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import com.icemetalpunk.psychicaltars.helpers.FakePlayerHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class PAEventHandler {
	private static final Field loveCauseField = ObfuscationReflectionHelper.findField(AnimalEntity.class,
			"field_146084_br");

	@SubscribeEvent
	public void babySpawnHandler(final BabyEntitySpawnEvent event) {
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
		ServerWorld server = animalParentA.getServer().overworld();
		FakePlayer nullBreeder = FakePlayerHelper.getOrCreate(server, "Breeder 0");
		UUID loveUUID;
		try {
			loveUUID = (UUID) loveCauseField.get(animalParentA);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
		Optional<FakePlayer> optionalPlayer = FakePlayerHelper.getByUUID(server, loveUUID);
		if (!optionalPlayer.isPresent()) {
			return;
		}
		FakePlayer player = optionalPlayer.get();
		CompoundNBT nbtTag = new CompoundNBT();
		animalChild.save(nbtTag);
		nbtTag.remove("UUID");
		for (int i = 1; i <= 4; ++i) {
			if (player == FakePlayerHelper.getOrCreate(server, "Breeder " + i)) {
				animalParentA.setInLove(nullBreeder);
				animalParentA.resetLove();
				animalParentB.setInLove(nullBreeder);
				animalParentB.resetLove();
				for (int j = 0; j < i; ++j) {
					Entity babyClone = EntityType.loadEntityRecursive(nbtTag, server, (baby) -> {
						return baby;
					});
					if (babyClone != null) {
						System.out.println("Spawned clone " + j + "!");
						DifficultyInstance diff = server.getCurrentDifficultyAt(animalChild.blockPosition());
						((AnimalEntity) babyClone).finalizeSpawn(server, diff, SpawnReason.BREEDING, null, null);
					} else {
						System.out.println("Failed to spawn clone " + j + "!");
					}
				}
				break;
			}
		}
	}
}
