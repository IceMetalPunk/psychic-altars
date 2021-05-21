package com.icemetalpunk.psychicaltars.blocks.altar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.MutableTriple;

import com.icemetalpunk.psychicaltars.blocks.IOmen.OmenTypes;
import com.icemetalpunk.psychicaltars.blocks.PABasicBlock;
import com.icemetalpunk.psychicaltars.multiblocks.InvalidAltarTypes;
import com.icemetalpunk.psychicaltars.multiblocks.MultiblockHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.patchouli.api.IMultiblock;

public abstract class PAAltarBlock extends PABasicBlock {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	protected ArrayList<IMultiblock> tiers = new ArrayList<>();
	protected HashMap<OmenTypes, Function<Integer, Integer>> maxOmens = new HashMap<>();
	public boolean isValid = false;
	public int tier = 0;
	public int range = 1;

	public PAAltarBlock(Properties props, String name) {
		super(props.noOcclusion(), name);
		this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, Boolean.valueOf(false)));

		this.maxOmens.put(OmenTypes.RANGE, tier -> 3);
		this.maxOmens.put(OmenTypes.SPEED, tier -> 4);
		this.maxOmens.put(OmenTypes.ANCHOR, tier -> 1);
		this.maxOmens.put(OmenTypes.PERSISTENCE, tier -> 1);
		this.maxOmens.put(OmenTypes.EFFICIENCY, tier -> 4);

		this.setMaxOmens();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		IParticleData part = this.getActiveParticle(this.tier, state);
		if (part != null && state.getValue(ACTIVE)) {
			double dx = (double) pos.getX() + rand.nextDouble();
			double dy = (double) pos.getY() + 0.5D;
			double dz = (double) pos.getZ() + rand.nextDouble();
			world.addParticle(part, dx, dy, dz, 0.5D, 0.5D, 0.5D);
		}

	}

	@Override
	public RenderType getRenderLayer() {
		return RenderType.translucent();
	}

	@Override
	public VoxelShape getVisualShape(BlockState p_230322_1_, IBlockReader p_230322_2_, BlockPos p_230322_3_,
			ISelectionContext p_230322_4_) {
		return VoxelShapes.empty();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
		return true;
	}

	public abstract void setTierMultiblocks();

	protected abstract void setMaxOmens();

	protected abstract int getBaseSpeed(int tier);

	protected abstract int getBaseRange(int tier);

	@Nullable
	protected IParticleData getActiveParticle(int tier, BlockState state) {
		return null;
	}

	protected abstract void operate(BlockState state, ServerWorld world, BlockPos pos, Random rand);

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult trace) {
		if (!world.isClientSide) {
			MutableTriple<InvalidAltarTypes, OmenTypes, Integer> info = new MutableTriple<>(InvalidAltarTypes.INVALID,
					OmenTypes.SPEED, 0);
			this.validate(state, (ServerWorld) world, pos, null, info);
			String omenWord = (info.getRight() == 1) ? "extra.psychicaltars.omen_singular"
					: "extra.psychicaltars.omen_plural";
			TranslationTextComponent text = new TranslationTextComponent(info.getLeft().getTranslationKey(),
					info.getRight(), info.getMiddle().getName(), new TranslationTextComponent(omenWord));
			UUID uuid = player.getUUID();
			MinecraftServer server = world.getServer();
			PlayerList list = server.getPlayerList();
			list.broadcastMessage(text, ChatType.CHAT, uuid);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext contexxt) {
		return this.defaultBlockState().setValue(ACTIVE, false);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> container) {
		container.add(ACTIVE);
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity player,
			ItemStack stack) {
		super.setPlacedBy(world, pos, state, player, stack);
		if (!world.isClientSide) {
			ServerWorld server = (ServerWorld) world;
			server.getBlockTicks().scheduleTick(pos, this, 1);
		}
	}

	public AxisAlignedBB getRangeBB(BlockPos pos) {
		return AxisAlignedBB.ofSize(1, 1, 1).move(pos).expandTowards(this.range, this.range, this.range);
	}

	public int validate(BlockState state, ServerWorld world, BlockPos pos, @Nullable Random rand,
			@Nullable MutableTriple<InvalidAltarTypes, OmenTypes, Integer> info) {
		MultiblockHelper.resetData();
		boolean wasValid = state.getValue(ACTIVE);
		this.isValid = false;
		this.tier = 0;
		this.range = 1;
		for (int i = this.tiers.size() - 1; i >= 0; --i) {
			if (this.tiers.get(i).validate(world, pos) != null) {
				this.isValid = true;
				this.tier = i + 1;
				break;
			}
		}

		if (this.isValid) {
			for (Entry<OmenTypes, Function<Integer, Integer>> max : this.maxOmens.entrySet()) {
				int count = MultiblockHelper.getOmenCount(max.getKey());
				if (count > max.getValue().apply(this.tier)) {
					this.isValid = false;
					if (info != null) {
						info.setLeft(InvalidAltarTypes.EXCESS_OMENS);
						info.setMiddle(max.getKey());
						info.setRight(max.getValue().apply(this.tier));
					}
					break;
				}
			}
		}

		if (this.isValid) {
			if (info != null) {
				info.setLeft(InvalidAltarTypes.NONE);
			}

			if (!wasValid) {
				world.setBlock(pos, world.getBlockState(pos).setValue(ACTIVE, true), 3);
			}

			int upgradeSpeed = MultiblockHelper.getOmenCount(OmenTypes.SPEED);
			int baseSpeed = this.getBaseSpeed(this.tier);
			return (int) Math.max(1, baseSpeed * (1 - 0.25 * upgradeSpeed));
		} else if (wasValid) {
			world.setBlock(pos, world.getBlockState(pos).setValue(ACTIVE, false), 3);
		}

		return 20;
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		int tickTime = this.validate(state, world, pos, rand, null);

		if (this.isValid) {
			int upgradeRange = MultiblockHelper.getOmenCount(OmenTypes.RANGE);
			int baseRange = this.getBaseRange(this.tier);
			this.range = (int) Math.max(1, baseRange * (1 << upgradeRange));

			BlockPos operationPos = pos;
			ArrayList<BlockPos> anchorPosList = MultiblockHelper.getOmenPositions(OmenTypes.ANCHOR);
			if (anchorPosList.size() > 0) {
				operationPos = anchorPosList.get(0);
			}
			this.operate(state, world, operationPos, rand);
		}
		world.getBlockTicks().scheduleTick(pos, this, tickTime);
	}
}
