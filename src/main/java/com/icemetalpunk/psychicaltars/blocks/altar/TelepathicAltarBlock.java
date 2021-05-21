package com.icemetalpunk.psychicaltars.blocks.altar;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.icemetalpunk.psychicaltars.PsychicAltars;
import com.icemetalpunk.psychicaltars.blocks.BlockRegistry;
import com.icemetalpunk.psychicaltars.blocks.IOmen.OmenTypes;
import com.icemetalpunk.psychicaltars.helpers.FakePlayerHelper;
import com.icemetalpunk.psychicaltars.multiblocks.MultiblockHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
		IMultiblock struct = api.makeMultiblock(
				new String[][] { new String[] { "AAA", "A0A", "AAA" }, new String[] { "SOS", "OIO", "SOS" } }, 'S',
				api.strictBlockMatcher((Block) BlockRegistry.get("omenstone")), 'I', MultiblockHelper.inventoryMatcher,
				'O', MultiblockHelper.omenMatcher, 'A', api.anyMatcher(), '0', api.looseBlockMatcher(this))
				.setSymmetrical(true);
		api.registerMultiblock(new ResourceLocation(PsychicAltars.MODID, "telepathic_altar_tier1"), struct);
		this.tiers.add(struct);
	}

	@Override
	protected void setMaxOmens() {
		this.maxOmens.put(OmenTypes.SPEED, tier -> {
			if (tier == 1) {
				return 3;
			}
			return 4;
		});
	}

	@Override
	protected int getBaseSpeed(int tier) {
		return 1200;
	}

	@Override
	protected int getBaseRange(int tier) {
		return 1;
	}

	@Override
	@Nullable
	protected IParticleData getActiveParticle(int tier, BlockState state) {
		if (tier == 1) {
			return ParticleTypes.HEART;
		}
		return null;
	}

	protected void makeFallInLove(ServerWorld world, BlockPos pos, TileEntity te) {
		LazyOptional<IItemHandler> handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		if (!handler.isPresent()) {
			return;
		}
		IItemHandler itemHandler = handler.resolve().get();

		int efficiencyLevel = MultiblockHelper.getOmenCount(OmenTypes.EFFICIENCY);

		AxisAlignedBB aabb = this.getRangeBB(pos);
		for (AnimalEntity ent : world.getEntitiesOfClass(AnimalEntity.class, aabb)) {
			if (ent.canFallInLove() && ent.getAge() >= 0) {
				for (int i = 0; i < itemHandler.getSlots(); ++i) {
					ItemStack item = itemHandler.getStackInSlot(i);
					if (ent.isFood(item) && itemHandler.extractItem(i, 1, false).getCount() > 0) {
						ent.setAge(0);
						PlayerEntity breedingFakePlayer = FakePlayerHelper.getOrCreate(world,
								"Breeder " + efficiencyLevel);
						System.out.println("Making breed for level " + efficiencyLevel);
						System.out.println("In world " + world);
						System.out.println("Player: " + breedingFakePlayer.getName());
						ent.setInLove(breedingFakePlayer);
						ent.setInLoveTime(60);
						break;
					}
				}
			}
		}
	}

	// TODO: Better model, then higher tiers

	@Override
	protected void operate(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		if (this.tier == 1) {
			ArrayList<Pair<BlockPos, TileEntity>> inventories = MultiblockHelper.getInventories();
			if (inventories.size() > 0) {
				makeFallInLove(world, pos, inventories.get(0).getRight());
			}
		}
	}
}
