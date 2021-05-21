package com.icemetalpunk.psychicaltars.blocks;

public interface IOmen {
	public enum OmenTypes {
		SPEED("speed"), RANGE("range"), ANCHOR("anchor"), EFFICIENCY("efficiency"), PERSISTENCE("persistence");
		private String name;

		private OmenTypes(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	public OmenTypes getType();
}
