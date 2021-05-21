package com.icemetalpunk.psychicaltars.blocks.altar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.icemetalpunk.psychicaltars.PsychicAltars;
import com.icemetalpunk.psychicaltars.blocks.BlockRegistry;
import com.icemetalpunk.psychicaltars.blocks.IOmen.OmenTypes;
import com.icemetalpunk.psychicaltars.helpers.AltarData;
import com.icemetalpunk.psychicaltars.helpers.FakePlayerHelper;
import com.icemetalpunk.psychicaltars.multiblocks.MultiblockHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;

public class TelepathicAltarBlock extends PAAltarBlock {

	// TODO: Add recipe for this block

	public TelepathicAltarBlock(Properties props, String name) {
		super(props, name);
	}

	@Override
	public void setTierMultiblocks() {
		// TODO: Add tier 2 & 3 structures
		IPatchouliAPI api = PatchouliAPI.get();
		IMultiblock struct1 = api.makeMultiblock(
				new String[][] { new String[] { "AAA", "A0A", "AAA" }, new String[] { "SOS", "OIO", "SOS" } }, 'S',
				api.strictBlockMatcher((Block) BlockRegistry.get("omenstone")), 'I', MultiblockHelper.inventoryMatcher,
				'O', MultiblockHelper.omenMatcher, 'A', api.anyMatcher(), '0', api.looseBlockMatcher(this))
				.setSymmetrical(true);
		api.registerMultiblock(new ResourceLocation(PsychicAltars.MODID, "telepathic_altar_tier1"), struct1);
		this.tiers.add(struct1);

		IMultiblock struct2 = api.makeMultiblock(
				new String[][] { new String[] { "UAAAU", "AAAAA", "AAAAA", "AAAAA", "UAAAU" },
						new String[] { "SAAAS", "AAAAA", "AA0AA", "AAAAA", "SAAAS" },
						new String[] { "SSOSS", "SSOSS", "OOAOO", "SSOSS", "SSOSS" } },
				'S', api.strictBlockMatcher((Block) BlockRegistry.get("omenstone")), 'U',
				api.strictBlockMatcher(Blocks.SOUL_SAND), 'O', MultiblockHelper.omenMatcher, 'A', api.anyMatcher(), '0',
				api.looseBlockMatcher(this)).setSymmetrical(true);
		api.registerMultiblock(new ResourceLocation(PsychicAltars.MODID, "telepathic_altar_tier2"), struct2);
		this.tiers.add(struct2);
	}

	@Override
	protected void setMaxOmens() {
		this.maxOmens.put(OmenTypes.SPEED, tier -> {
			switch (tier) {
				case 1:
					return 3;
				case 2:
					return 4;
				default:
					return 4;
			}
		});

		this.maxOmens.put(OmenTypes.EFFICIENCY, tier -> {
			switch (tier) {
				case 1:
					return 4;
				case 2:
					return 5;
				default:
					return 4;
			}
		});
	}

	@Override
	protected int getBaseSpeed(int tier) {
		switch (tier) {
			case 1:
				return 1200;
			case 2:
				return 600;
			default:
				return 1200;
		}
	}

	@Override
	protected int getBaseRange(int tier) {
		switch (tier) {
			case 1:
				return 1;
			case 2:
				return 3;
			default:
				return 1;
		}
	}

	@Override
	protected void setParticleTypes() {
		ArrayList<Pair<IParticleData, Vector3d>> tier1list = new ArrayList<>();
		tier1list.add(Pair.of(ParticleTypes.HEART, new Vector3d(0.5, 0.5, 0.5)));
		this.particles.put(1, tier1list);

		ArrayList<Pair<IParticleData, Vector3d>> tier2list = new ArrayList<>();
		tier2list.add(Pair.of(ParticleTypes.SOUL, new Vector3d(0.2, 0.1, 0.2)));
		tier2list.add(Pair.of(ParticleTypes.SOUL, new Vector3d(-0.2, 0.1, -0.2)));
		tier2list.add(Pair.of(ParticleTypes.SOUL, new Vector3d(0.2, 0.1, -0.2)));
		tier2list.add(Pair.of(ParticleTypes.SOUL, new Vector3d(-0.2, 0.1, 0.2)));
		this.particles.put(2, tier2list);
	}

	@Override
	protected double getParticleDispersion(int tier) {
		switch (tier) {
			case 1:
				return 1.0D;
			case 2:
				return 0.0D;
			default:
				return 1.0D;
		}
	}

	protected void makeFallInLove(ServerWorld world, BlockPos pos, TileEntity te, AltarData altarData) {
		LazyOptional<IItemHandler> handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		if (!handler.isPresent()) {
			return;
		}
		IItemHandler itemHandler = handler.resolve().get();

		int efficiencyLevel = MultiblockHelper.getOmenCount(OmenTypes.EFFICIENCY);

		AxisAlignedBB aabb = this.getRangeBB(pos, altarData.range);
		for (AnimalEntity ent : world.getEntitiesOfClass(AnimalEntity.class, aabb)) {
			if (ent.canFallInLove() && ent.getAge() >= 0) {
				for (int i = 0; i < itemHandler.getSlots(); ++i) {
					ItemStack item = itemHandler.getStackInSlot(i);
					if (ent.isFood(item) && itemHandler.extractItem(i, 1, false).getCount() > 0) {
						ent.setAge(0);
						PlayerEntity breedingFakePlayer = FakePlayerHelper.getOrCreate(world,
								"Breeder " + efficiencyLevel);
						ent.setInLove(breedingFakePlayer);
						ent.setInLoveTime(60);
						break;
					}
				}
			}
		}
	}

	protected void slowMobs(ServerWorld world, BlockPos pos, AltarData altarData) {
		int efficiencyLevel = MultiblockHelper.getOmenCount(OmenTypes.EFFICIENCY);
		if (efficiencyLevel > 4) {
			efficiencyLevel = 10;
		}
		AxisAlignedBB aabb = this.getRangeBB(pos, altarData.range);
		List<LivingEntity> mobs = world.getEntitiesOfClass(LivingEntity.class, aabb,
				(ent) -> !(ent instanceof PlayerEntity));
		for (LivingEntity ent : mobs) {
			EffectInstance eff = new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 200, efficiencyLevel);
			if (ent.hasEffect(Effects.MOVEMENT_SLOWDOWN)) {
				ent.getEffect(Effects.MOVEMENT_SLOWDOWN).update(eff);
			} else {
				ent.addEffect(eff);
			}
		}
	}

	// TODO: Better model, then higher tiers

	@Override
	protected void operate(BlockState state, ServerWorld world, BlockPos pos, Random rand, AltarData altarData) {
		if (altarData.tier == 1) {
			ArrayList<Pair<BlockPos, TileEntity>> inventories = MultiblockHelper.getInventories();
			if (inventories.size() > 0) {
				makeFallInLove(world, pos, inventories.get(0).getRight(), altarData);
			}
		} else if (altarData.tier == 2) {
			slowMobs(world, pos, altarData);
		}
	}
}
