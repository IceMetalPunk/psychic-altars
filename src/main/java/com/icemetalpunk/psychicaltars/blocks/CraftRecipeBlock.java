package com.icemetalpunk.psychicaltars.blocks;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

// TODO: Model, recipe, & implement this when Apport Altar is added

public class CraftRecipeBlock extends PACraftContainerBlock {
	protected String containerName;

	public CraftRecipeBlock(Properties props, String name) {
		super(props, name);
		this.containerName = name;
	}

	@Override
	public ITextComponent getContainerName() {
		return new TranslationTextComponent("container.psychicaltars." + this.containerName);
	}
}
