package com.icemetalpunk.psychicaltars.blocks;

import com.icemetalpunk.psychicaltars.PsychicAltars;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;

public class PABasicBlock extends Block implements PABlock {
	public final BlockItem blockItem;

	public PABasicBlock(Properties props, String name, BlockItem item) {
		super(props);
		this.setRegistryName(PsychicAltars.MODID, name);
		this.blockItem = item;
	}

	public PABasicBlock(Properties props, String name) {
		super(props);
		this.setRegistryName(PsychicAltars.MODID, name);
		this.blockItem = new BlockItem(this, new Item.Properties().tab(PsychicAltars.TAB));
		this.blockItem.setRegistryName(PsychicAltars.MODID, name);
	}

	@Override
	public void registerBlockItem(Register<Item> event) {
		if (this.blockItem != null) {
			event.getRegistry().register((BlockItem) this.blockItem);
		}
	}
}
