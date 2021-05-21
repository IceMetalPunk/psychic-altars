package com.icemetalpunk.psychicaltars.blocks.altar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;

import com.icemetalpunk.psychicaltars.blocks.IOmen.OmenTypes;
import com.icemetalpunk.psychicaltars.blocks.PABasicBlock;
import com.icemetalpunk.psychicaltars.helpers.AltarData;
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
import net.minecraft.util.math.vector.Vector3d;
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
	protected HashMap<Integer, ArrayList<Pair<IParticleData, Vector3d>>> particles = new HashMap<>();

	public PAAltarBlock(Properties props, String name) {
		super(props.noOcclusion(), name);
		this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, Boolean.valueOf(false)));

		this.maxOmens.put(OmenTypes.RANGE, tier -> 3);
		this.maxOmens.put(OmenTypes.SPEED, tier -> 4);
		this.maxOmens.put(OmenTypes.ANCHOR, tier -> 1);
		this.maxOmens.put(OmenTypes.PERSISTENCE, tier -> 1);
		this.maxOmens.put(OmenTypes.EFFICIENCY, tier -> 4);

		this.setMaxOmens();
		this.setParticleTypes();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		Optional<AltarData> validation = this.validate(state, world, pos, rand, null);
		validation.ifPresent(altarData -> {
			if (state.getValue(ACTIVE) && this.particles.containsKey(altarData.tier)) {
				ArrayList<Pair<IParticleData, Vector3d>> partInfo = this.particles.get(altarData.tier);
				for (Pair<IParticleData, Vector3d> info : partInfo) {
					IParticleData part = info.getLeft();
					Vector3d destination = info.getRight();
					double dispersion = this.getParticleDispersion(altarData.tier);
					int xDir = rand.nextInt(3) - 1;
					int yDir = rand.nextInt(3) - 1;
					double dx = (double) pos.getX() + 0.5D + rand.nextDouble() * dispersion * xDir;
					double dy = (double) pos.getY() + 0.5D;
					double dz = (double) pos.getZ() + 0.5D + rand.nextDouble() * dispersion * yDir;
					world.addParticle(part, dx, dy, dz, destination.x, destination.y, destination.z);
				}
			}
		});
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

	protected abstract void setParticleTypes();

	protected double getParticleDispersion(int tier) {
		return 1.0D;
	}

	protected abstract int getBaseSpeed(int tier);

	protected abstract int getBaseRange(int tier);

	protected abstract void operate(BlockState state, ServerWorld world, BlockPos pos, Random rand,
			AltarData altarData);

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

	public AxisAlignedBB getRangeBB(BlockPos pos, int range) {
		return AxisAlignedBB.ofSize(1, 1, 1).move(pos).inflate(range);
	}

	public Optional<AltarData> validate(BlockState state, World world, BlockPos pos, @Nullable Random rand,
			@Nullable MutableTriple<InvalidAltarTypes, OmenTypes, Integer> info) {
		boolean wasValid = state.getValue(ACTIVE);
		boolean isValid = false;
		int tier = 0;
		int range = 1;
		for (int i = this.tiers.size() - 1; i >= 0; --i) {
			MultiblockHelper.resetData();
			if (this.tiers.get(i).validate(world, pos) != null) {
				isValid = true;
				tier = i + 1;
				break;
			}
		}

		if (isValid) {
			for (Entry<OmenTypes, Function<Integer, Integer>> max : this.maxOmens.entrySet()) {
				int count = MultiblockHelper.getOmenCount(max.getKey());
				if (count > max.getValue().apply(tier)) {
					isValid = false;
					if (info != null) {
						info.setLeft(InvalidAltarTypes.EXCESS_OMENS);
						info.setMiddle(max.getKey());
						info.setRight(max.getValue().apply(tier));
					}
					break;
				}
			}
		}

		if (isValid) {
			if (info != null) {
				info.setLeft(InvalidAltarTypes.NONE);
			}

			if (!wasValid) {
				world.setBlock(pos, world.getBlockState(pos).setValue(ACTIVE, true), 3);
			}

			int upgradeSpeed = MultiblockHelper.getOmenCount(OmenTypes.SPEED);
			int baseSpeed = this.getBaseSpeed(tier);

			int upgradeRange = MultiblockHelper.getOmenCount(OmenTypes.RANGE);
			int baseRange = this.getBaseRange(tier);
			range = (int) Math.max(1, baseRange * (1 << upgradeRange));

			return Optional.of(new AltarData(tier, range, (int) Math.max(1, baseSpeed * (1 - 0.25 * upgradeSpeed))));
		} else if (wasValid) {
			world.setBlock(pos, world.getBlockState(pos).setValue(ACTIVE, false), 3);
		}

		if (!isValid) {
			return Optional.empty();
		}
		return Optional.of(new AltarData(tier, range, 20));
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		Optional<AltarData> validation = this.validate(state, world, pos, rand, null);

		if (validation.isPresent()) {
			AltarData altarData = validation.get();

			BlockPos operationPos = pos;
			ArrayList<BlockPos> anchorPosList = MultiblockHelper.getOmenPositions(OmenTypes.ANCHOR);
			if (anchorPosList.size() > 0) {
				operationPos = anchorPosList.get(0);
			}
			this.operate(state, world, operationPos, rand, altarData);
			world.getBlockTicks().scheduleTick(pos, this, altarData.speed);
		} else {
			world.getBlockTicks().scheduleTick(pos, this, 20);
		}
	}
}
