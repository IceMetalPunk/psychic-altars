package com.icemetalpunk.psychicaltars.items;

import com.icemetalpunk.psychicaltars.PsychicAltars;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;

public class PAItem extends Item {
	public PAItem(Properties props, String name) {
		super(props.tab(PsychicAltars.TAB));
		this.setRegistryName(PsychicAltars.MODID, name);
	}

	public PAItem(String name) {
		this(new Item.Properties(), name);
	}

	public void register(final RegistryEvent.Register<Item> event) {
		event.getRegistry().register(this);
	}
}
