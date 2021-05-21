package com.icemetalpunk.psychicaltars.blocks;

import java.util.Random;

import com.icemetalpunk.psychicaltars.PsychicAltars;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegistryEvent.Register;

public abstract class PACraftContainerBlock extends ContainerBlock implements PABlock {
	public final BlockItem blockItem;

	public PACraftContainerBlock(Properties props, String name, BlockItem item) {
		super(props);
		this.setRegistryName(PsychicAltars.MODID, name);
		this.blockItem = item;
		this.registerDefaultState(this.stateDefinition.any());
	}

	public PACraftContainerBlock(Properties props, String name) {
		super(props);
		this.setRegistryName(PsychicAltars.MODID, name);
		this.blockItem = new BlockItem(this, new Item.Properties().tab(PsychicAltars.TAB));
		this.blockItem.setRegistryName(PsychicAltars.MODID, name);
		this.registerDefaultState(this.stateDefinition.any());
	}

	@Override
	public void registerBlockItem(Register<Item> event) {
		if (this.blockItem != null) {
			event.getRegistry().register((BlockItem) this.blockItem);
		}
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult trace) {
		if (world.isClientSide) {
			return ActionResultType.SUCCESS;
		} else {
			TileEntity tileentity = world.getBlockEntity(pos);
			if (tileentity instanceof DispenserTileEntity) {
				player.openMenu((DispenserTileEntity) tileentity);
			}

			return ActionResultType.CONSUME;
		}
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos posFrom,
			boolean flag) {
		// No-op override
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		// No-op override
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation currentRotation) {
		return state;
	}

	@Override
	public BlockState mirror(BlockState state, Mirror currentMirror) {
		return state;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateBuilder) {
		// No-op override
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return new DispenserTileEntity();
	}

	public abstract ITextComponent getContainerName();

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity player, ItemStack stack) {
		TileEntity tileentity = world.getBlockEntity(pos);
		if (tileentity instanceof DispenserTileEntity) {
			if (stack.hasCustomHoverName()) {
				((DispenserTileEntity) tileentity).setCustomName(stack.getHoverName());
			} else {
				((DispenserTileEntity) tileentity).setCustomName(this.getContainerName());
			}
		}
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean flag) {
		if (!state.is(newState.getBlock())) {
			TileEntity tileentity = world.getBlockEntity(pos);
			if (tileentity instanceof DispenserTileEntity) {
				InventoryHelper.dropContents(world, pos, (DispenserTileEntity) tileentity);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, world, pos, newState, flag);
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
		return Container.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
}
