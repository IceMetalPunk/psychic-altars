package com.icemetalpunk.psychicaltars.multiblocks;

import com.icemetalpunk.psychicaltars.PsychicAltars;

public enum InvalidAltarTypes {
	INVALID("not_found"), EXCESS_OMENS("excess_omens"), NONE("none");
	String translationKey = "unknown";

	private InvalidAltarTypes(String key) {
		this.translationKey = key;
	}

	public String getTranslationKey() {
		return "errors." + PsychicAltars.MODID + ".invalid_altar." + this.translationKey;
	}
}