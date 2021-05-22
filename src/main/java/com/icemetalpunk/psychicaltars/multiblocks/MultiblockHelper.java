package com.icemetalpunk.psychicaltars.multiblocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.WeakHashMap;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.icemetalpunk.psychicaltars.blocks.BlockRegistry;
import com.icemetalpunk.psychicaltars.blocks.IOmen.OmenTypes;
import com.icemetalpunk.psychicaltars.blocks.OmenBlock;
import com.icemetalpunk.psychicaltars.blocks.PABlock;
import com.icemetalpunk.psychicaltars.blocks.altar.PAAltarBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import vazkii.patchouli.api.IStateMatcher;

public class MultiblockHelper {
	public static final ArrayList<BlockPos> EMPTY_POS_LIST = new ArrayList<>();
	public static IStateMatcher omenMatcher;
	public static IStateMatcher inventoryMatcher;
	private static final WeakHashMap<OmenTypes, ArrayList<BlockPos>> omenData = new WeakHashMap<>();
	private static final ArrayList<Pair<BlockPos, TileEntity>> inventoryData = new ArrayList<>();

	private static class InventoryMatcher implements IStateMatcher {
		@Override
		public BlockState getDisplayedState(int ticks) {
			return Blocks.BARREL.defaultBlockState();
		}

		@Override
		public TriPredicate<IBlockReader, BlockPos, BlockState> getStatePredicate() {
			return (world, pos, state) -> {
				boolean hasTileEntity = state.hasTileEntity() || (state.getBlock() instanceof ITileEntityProvider);
				if (hasTileEntity) {
					TileEntity te = world.getBlockEntity(pos);
					if (te != null) {
						LazyOptional<IItemHandler> handler = te
								.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
						if (handler.isPresent()) {
							inventoryData.add(Pair.of(pos, te));
							return true;
						}
					}
				}
				return false;
			};
		}
	}

	private static class OmenMatcher implements IStateMatcher {
		@Override
		public BlockState getDisplayedState(int ticks) {
			ImmutableList<OmenBlock> omens = OmenBlock.getAllOmens();
			int tickCycle = ticks / 10;
			return omens.get(tickCycle % omens.size()).defaultBlockState();
		}

		@Override
		public TriPredicate<IBlockReader, BlockPos, BlockState> getStatePredicate() {
			return (world, pos, state) -> {
				boolean isOmen = state.getBlock() instanceof OmenBlock;
				if (isOmen) {
					OmenTypes type = ((OmenBlock) state.getBlock()).getType();
					if (!omenData.containsKey(type)) {
						ArrayList<BlockPos> list = new ArrayList<>();
						list.add(pos);
						omenData.put(type, list);
					} else {
						ArrayList<BlockPos> list = omenData.get(type);
						list.add(pos);
					}
				}
				boolean isOmenstone = state.getBlock() == BlockRegistry.get("omenstone").get();
				return isOmen || isOmenstone;
			};
		}
	}

	public static void registerMatchers() {
		omenMatcher = new OmenMatcher();
		inventoryMatcher = new InventoryMatcher();
	}

	public static void registerMultiblocks(Collection<PABlock> blocks) {
		for (PABlock block : blocks) {
			if (block instanceof PAAltarBlock) {
				((PAAltarBlock) block).setTierMultiblocks();
			}
		}
	}

	public static void resetData() {
		omenData.clear();
		inventoryData.clear();
	}

	public static int getOmenCount(OmenTypes type) {
		return getOmenPositions(type).size();
	}

	public static ArrayList<BlockPos> getOmenPositions(OmenTypes type) {
		if (!omenData.containsKey(type)) {
			return EMPTY_POS_LIST;
		}
		return omenData.get(type);
	}

	public static int getInventoryCount() {
		return inventoryData.size();
	}

	public static ArrayList<Pair<BlockPos, TileEntity>> getInventories() {
		return inventoryData;
	}
}
