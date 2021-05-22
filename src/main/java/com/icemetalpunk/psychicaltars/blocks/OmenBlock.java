package com.icemetalpunk.psychicaltars.blocks;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public class OmenBlock extends PABasicBlock implements IOmen {
	protected OmenTypes type;
	private static ArrayList<OmenBlock> allOmens = new ArrayList<>();

	public OmenBlock(String name, OmenTypes type) {
		super(AbstractBlock.Properties.copy((Block) BlockRegistry.get("omenstone").get()), name);
		this.type = type;
		allOmens.add(this);
	}

	public static ImmutableList<OmenBlock> getAllOmens() {
		return ImmutableList.copyOf(allOmens);
	}

	@Override
	public OmenTypes getType() {
		return this.type;
	}
}
